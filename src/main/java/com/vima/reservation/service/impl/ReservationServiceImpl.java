package com.vima.reservation.service.impl;

import com.vima.gateway.ReservationRequest;
import com.vima.gateway.ReservationStatus;
import com.vima.reservation.model.Reservation;
import com.vima.reservation.repository.ReservationRepository;
import com.vima.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository repository;

    @Override
    public Reservation create(Reservation newReservation) {
       return repository.save(newReservation);
    }

    @Override
    public Reservation findById(String id) {
        return  repository.findById(UUID.fromString(id)).get();
    }

//    @Override
//    public String reservationResponse(UUID id) {
//        retu
//    }

    @Override
    public String cancelReservation(String id) {
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
