package com.vima.reservation.service;


import com.vima.gateway.AccommodationResponse;
import com.vima.gateway.HostResponse;
import com.vima.gateway.ReservationRequest;
import com.vima.gateway.SearchRequest;
import com.vima.gateway.SearchReservationRequest;
import com.vima.reservation.model.Reservation;

import java.util.List;
import java.util.UUID;

import communication.UserDetailsResponse;

public interface ReservationService {

    Reservation create(Reservation request, AccommodationResponse accommodation, UserDetailsResponse host);

    Reservation findById(UUID id);

    List<String> search(SearchReservationRequest request);

    List<Reservation> findAll();

    String hostResponse(HostResponse response, UserDetailsResponse guest, AccommodationResponse accommodation);

    String cancelReservation(UUID id, UserDetailsResponse host, AccommodationResponse accommodation);

    List<Reservation> findAllByUser(String id, String role);

    boolean checkIfGuestHasActiveReservations(String userId);

    boolean checkIfHostHasActiveReservations(String hostId);

    boolean isHostDistinguished(String hostId);
}
