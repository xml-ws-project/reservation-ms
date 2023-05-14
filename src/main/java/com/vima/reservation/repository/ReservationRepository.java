package com.vima.reservation.repository;

import com.vima.reservation.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, UUID> {

    List<Reservation> findAll();

    @Query("{'userId' : ?0, $or: [ { status: PENDING }, { status: ACCEPTED } ] } ")
    List<Reservation> findAllByGuest(String id);

    @Query("{ 'accomInfo.hostId' : ?0, 'status' : PENDING }")
    List<Reservation> findAllByHost(String id);
}
