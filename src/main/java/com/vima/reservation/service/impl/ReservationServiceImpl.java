package com.vima.reservation.service.impl;

import com.vima.gateway.AccommodationResponse;
import com.vima.gateway.HostResponse;
import com.vima.gateway.ReservationStatus;
import com.vima.gateway.SearchReservationRequest;
import com.vima.reservation.converter.LocalDateConverter;
import com.vima.reservation.model.DateRange;
import com.vima.reservation.model.Reservation;
import com.vima.reservation.repository.ReservationRepository;
import com.vima.reservation.service.ReservationService;
import com.vima.reservation.util.email.EmailService;

import communication.UserDetailsResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository repository;
    private final EmailService emailService;

    @Override
    public Reservation create(Reservation newReservation, AccommodationResponse accommodation, UserDetailsResponse host) {
        if(accommodation.getAutomaticAcceptance())
            realizeReservationAcceptance(newReservation);
        notifyReservationRequest(newReservation, accommodation, host);
        return repository.save(newReservation);
    }

    private void notifyReservationRequest(Reservation newReservation, AccommodationResponse accommodation, UserDetailsResponse host) {
        if (host.getNotificationOptions().getReservationRequest()) {
            String subject = "Reservation request notification";
            String body = "Request information" +
                "\n\n" + "Desired date: " + newReservation.getDesiredDate() + "\n"
                + "Number of guests: " + newReservation.getNumOfGuests() + "\n"
                + "Accommodation: " + accommodation.getName();
            emailService.sendSimpleMail(host.getUsername(), subject, body);
        }
    }

    @Override
    public Reservation findById(UUID id) {
        return repository.findById(id).get();
    }

    @Override
    public List<Reservation> findAll() {
        return repository.findAll();
    }

    @Override
    public String hostResponse(HostResponse response, UserDetailsResponse guest, AccommodationResponse accommodation) {
        var reservation = findById(UUID.fromString(response.getId()));
        if(reservation == null)
            return "Reservation not found";
        return executeHostResponse(reservation, response.getAccept(), guest, accommodation);
    }

    private String executeHostResponse(Reservation reservation, boolean accept, UserDetailsResponse guest, AccommodationResponse accommodation){
        if(accept)
            realizeReservationAcceptance(reservation);
        else
            reservation.setStatus(ReservationStatus.DECLINED);
        repository.save(reservation);
        notifyGuest(reservation, accept, guest, accommodation);
        return "Reservation successfully " + (accept ? "accepted!" : "declined.");
    }

    private void notifyGuest(Reservation reservation, boolean accept, UserDetailsResponse guest, AccommodationResponse accommodation) {
        if (guest.getNotificationOptions().getHostsReservationAnswer()) {
            String subject = "Host reservation answer notification";
            String body = "The request for booking accommodation " +
                accommodation.getName() + ", dated " + reservation.getDesiredDate() +
                ", has been " + (accept ? "accepted" : "declined") + "by the host.";
            emailService.sendSimpleMail(guest.getUsername(), subject, body);
        }
    }

    private void realizeReservationAcceptance(Reservation reservation){
        reservation.setStatus(ReservationStatus.ACCEPTED);
        cancelAllOverlapping(reservation.getDesiredDate());
    }

    private void cancelAllOverlapping(DateRange desiredDate){
        var all = repository.findAll();
        all.forEach(item ->{
            if(item.getDesiredDate().getStart().isBefore(desiredDate.getEnd()) && item.getDesiredDate().getEnd().isAfter(desiredDate.getStart())){
                item.setStatus(ReservationStatus.DECLINED);
                repository.save(item);
            }
        });
    }

    @Override
    public String cancelReservation(UUID id, UserDetailsResponse host, AccommodationResponse accommodation) {
        var reservation = findById(id);
        if(reservation == null)
            return "Reservation not found.";
        notifyAboutCancellation(reservation, accommodation, host);
        return executeCancelation(reservation);
    }

    private void notifyAboutCancellation(Reservation reservation, AccommodationResponse accommodation, UserDetailsResponse host) {
        if (host.getNotificationOptions().getReservationCancellation()) {
            String subject = "Reservation cancellation notification";
            String body = "Cancellation information" +
                "\n\n" + "Desired date: " + reservation.getDesiredDate() + "\n"
                + "Number of guests: " + reservation.getNumOfGuests() + "\n"
                + "Accommodation: " + accommodation.getName();
            emailService.sendSimpleMail(host.getUsername(), subject, body);
        }
    }

    private String executeCancelation(Reservation reservation){
        switch (reservation.getStatus()){
            case  PENDING:
                return cancelPending(reservation);
            case  ACCEPTED:
                return  cancelAccepted(reservation);
            default:
                return "Something went wrong.";
        }
    }

    private String cancelPending(Reservation reservation){
        repository.delete(reservation);
        return "Reservation successfully deleted.";
    }

    private String cancelAccepted(Reservation reservation){
        if(LocalDate.now().isAfter(reservation.getDesiredDate().getStart().minusDays(2))){
            return "You can't cancel this reservation, it to late.";
        }
//        reservation.setStatus(ReservationStatus.CANCELED_BY_GUEST);
        repository.delete(reservation);
        //dobaviti usera i dodati mu +1 otkazivanje;
        return "Reservation successfully canceled.";
    }

    @Override
    public List<Reservation> findAllByUser(String id, String role) {
        if(role.equals("HOST"))
            return repository.findAllByHost(id);

        return repository.findAllByGuest(id);
    }

    @Override
    public boolean checkIfGuestHasActiveReservations(final String userId) {
        return repository.findGuestsActiveReservations(userId).size() != 0;
    }

    @Override
    public boolean checkIfHostHasActiveReservations(final String hostId) {
        return repository.findHostActiveReservations(hostId).size() != 0;
    }

    public List<String> search(final SearchReservationRequest request) {
        LocalDate periodStart = LocalDateConverter.convertGoogleTimeStampToLocalDate(request.getPeriod().getStart());
        LocalDate periodEnd = LocalDateConverter.convertGoogleTimeStampToLocalDate(request.getPeriod().getEnd());
        List<Reservation> resultList = repository.searchReservations(ReservationStatus.ACCEPTED, request.getCountry(), request.getCity(), request.getGuests(), periodStart, periodEnd);
        List<String> accommodationIds = new ArrayList<>();
        resultList.forEach(reservation -> {
            accommodationIds.add(reservation.getAccomInfo().getAccomId());
        });
        return accommodationIds;
    }
}
