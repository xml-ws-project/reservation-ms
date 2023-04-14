package com.vima.reservation.service.impl;

import com.vima.reservation.model.AccommodationAvailability;
import com.vima.reservation.repository.AvailabilityRepository;
import com.vima.reservation.service.AvailabilityService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

	private final AvailabilityRepository availabilityRepository;

	@Override
	public AccommodationAvailability create(final AccommodationAvailability availability) {
		return availabilityRepository.save(availability);
	}

	@Override
	public AccommodationAvailability update(final AccommodationAvailability availability) {
		return availabilityRepository.save(availability);
	}

	@Override
	public AccommodationAvailability getById(final String id) {
		return availabilityRepository.findById(id).get();
	}

	@Override
	public void deleteById(final String id) {
		availabilityRepository.deleteById(id);
	}
}
