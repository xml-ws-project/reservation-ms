package com.vima.reservation.service;

import com.vima.reservation.model.AccommodationAvailability;

public interface AvailabilityService {

	AccommodationAvailability create(AccommodationAvailability availability);
	AccommodationAvailability update(AccommodationAvailability availability);
	AccommodationAvailability getById(String id);
	void deleteById(String id);
}
