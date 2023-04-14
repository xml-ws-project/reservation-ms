package com.vima.reservation.controller;

import com.vima.reservation.model.AccommodationAvailability;
import com.vima.reservation.service.AvailabilityService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

	private final AvailabilityService availabilityService;

	@PostMapping("/availability")
	public ResponseEntity<AccommodationAvailability> create(@RequestBody AccommodationAvailability availability) {
		var createdAvailability = availabilityService.create(availability);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdAvailability);
	}

	@DeleteMapping("/availability/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") final String id) {
		availabilityService.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/availability/{id}")
	public ResponseEntity<AccommodationAvailability> getById(@PathVariable("id") final String id) {
		return ResponseEntity.status(HttpStatus.OK).body(availabilityService.getById(id));
	}
}
