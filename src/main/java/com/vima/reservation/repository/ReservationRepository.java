package com.vima.reservation.repository;

import com.vima.reservation.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, UUID> {
    Optional<Reservation> findById(UUID id);

    List<Reservation> findAll();
}
