package com.vima.reservation.model;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class AccommodationAvailability {

	@Id
	String id;
	String accommodationId;
	LocalDate start;
	LocalDate end;
	Double price;
	String country;
	String city;
}
