package com.vima.reservation.service.impl;

import com.vima.gateway.ReservationRequest;
import com.vima.reservation.model.Reservation;
import com.vima.reservation.repository.ReservationRepository;
import com.vima.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public Reservation findById(UUID id) {
        return null;
    }
}
