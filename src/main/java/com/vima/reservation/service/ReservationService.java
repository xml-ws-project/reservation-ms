package com.vima.reservation.service;


import com.vima.gateway.ReservationRequest;
import com.vima.reservation.model.Reservation;

import java.util.UUID;

public interface ReservationService {

    Reservation create(Reservation request);
    Reservation findById(UUID id);
}
