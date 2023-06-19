package com.vima.reservation.repository;

import com.vima.gateway.ReservationStatus;
import com.vima.reservation.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @Query("{ 'status' : '?0'," +
        " 'accomInfo.country' : { $regex : /^?1/, $options: 'i' }," +
        " 'accomInfo.city' : { $regex : /^?2/, $options: 'i' }," +
        " 'accomInfo.minGuests' : { $lte : ?3 }," +
        " 'accomInfo.maxGuests' : { $gte : ?3 }," +
        " $or : [ {$and: [{'desiredDate.start' : { $lt : ?4 }}, {'desiredDate.end' : { $gt : ?4 }}] }," +
        " {$and: [{'desiredDate.start' : { $lt : ?5 }}, {'desiredDate.end' : { $gt : ?5 }}] } ] }")
    List<Reservation> searchReservations(ReservationStatus status, String country, String city, int guests, LocalDate periodStart, LocalDate periodEnd);

    @Query("{ 'status' : ACCEPTED, 'userId' : ?0 }")
    public List<Reservation> findGuestsActiveReservations(String userId);

    @Query("{ 'status' : ACCEPTED, 'accomInfo.hostId' : ?0 }")
    public List<Reservation> findHostActiveReservations(String hostId);

    @Query("{ 'status' : DECLINED, 'accomInfo.hostId' : ?0 }")
    public List<Reservation> findHostCancelledReservations(String hostId);
}
