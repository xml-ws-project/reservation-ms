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

    @Query("{ 'status' : '?0'," +
        " 'accomInfo.country' : { $regex : /^?1/, $options: 'i' }," +
        " 'accomInfo.city' : { $regex : /^?2/, $options: 'i' }," +
        " 'accomInfo.minGuests' : { $lte : ?3 }," +
        " 'accomInfo.maxGuests' : { $gte : ?3 }," +
        " $or : [ {$and: [{'desiredDate.start' : { $lte : ?4 }}, {'desiredDate.end' : { $gte : ?4 }}] }," +
        " {$and: [{'desiredDate.start' : { $lte : ?5 }}, {'desiredDate.end' : { $gte : ?5 }}] } ] }")
    List<Reservation> searchReservations(ReservationStatus status, String country, String city, int guests, LocalDate periodStart, LocalDate periodEnd);
}

//$or : [{ 'desiredDate.start' : { $gt : '?4', $lt : '?5' } }, { 'desiredDate.end' : { $gt : '?4', $lt : '?5' } }]