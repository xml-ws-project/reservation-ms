package com.vima.reservation.service.impl;

import com.vima.gateway.ReservationRequest;
import com.vima.gateway.ReservationStatus;
import com.vima.reservation.model.DateRange;
import com.vima.reservation.model.Reservation;
import com.vima.reservation.repository.ReservationRepository;
import com.vima.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository repository;

    @Override
    public Reservation create(Reservation newReservation, boolean isAutomatic) {
        if(isAutomatic)
            realizeHostAcceptance(newReservation);

        return repository.save(newReservation);
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
    public String hostResponse(UUID id, boolean accept) {
        var reservation = findById(id);
        if(reservation == null)
            return "Reservation not found";

        return executeHostResponse(reservation, accept);
    }

    private String executeHostResponse(Reservation reservation, boolean accept){
        if(accept){
            realizeHostAcceptance(reservation);
        }
        else
            reservation.setStatus(ReservationStatus.DECLINED);


        repository.save(reservation);
        return "Reservation successfully " + (accept ? "accepted!" : "declined.");
    }

    private void realizeHostAcceptance(Reservation reservation){
        reservation.setStatus(ReservationStatus.ACCEPTED);
        cancelAllOverlapping(reservation.getDesiredDate());
    }

    private void cancelAllOverlapping(DateRange desiredDate){
    }

    @Override
    public String cancelReservation(UUID id) {
        var reservation = findById(id);
        if(reservation == null)
            return "Reservation not found.";

        return executeCancelation(reservation);
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
        reservation.setStatus(ReservationStatus.CANCELED_BY_GUEST);
        repository.save(reservation);
        //dobaviti usera i dodati mu +1 otkazivanje;
        return "Reservation successfully canceled.";
    }
}