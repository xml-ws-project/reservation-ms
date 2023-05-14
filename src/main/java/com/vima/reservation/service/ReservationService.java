package com.vima.reservation.service;


import com.vima.gateway.ReservationRequest;
import com.vima.gateway.SearchRequest;
import com.vima.gateway.SearchReservationRequest;
import com.vima.reservation.model.Reservation;

import java.util.List;
import java.util.UUID;

public interface ReservationService {

    Reservation create(Reservation request, boolean isAutomatic);

    Reservation findById(UUID id);

    List<String> search(SearchReservationRequest request);

    List<Reservation> findAll();

    String hostResponse(UUID id, boolean accept);

    String cancelReservation(UUID id);
    List<Reservation> checkIfHostHasActiveReservations(String hostId);
}
