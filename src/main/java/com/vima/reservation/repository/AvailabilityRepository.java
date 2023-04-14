package com.vima.reservation.repository;

import com.vima.reservation.model.AccommodationAvailability;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepository extends MongoRepository<AccommodationAvailability, String> {
}
