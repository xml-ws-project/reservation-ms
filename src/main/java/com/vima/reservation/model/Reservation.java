package com.vima.reservation.model;

import com.vima.reservation.model.enums.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;


@Document("reservations")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Reservation {

    @Id
    UUID Id;

    int numOfGuests;

    Status status;

    AccommodationInfo accomInfo;

    DateRange desiredDate;

    String userId;
}
