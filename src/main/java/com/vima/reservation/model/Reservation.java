package com.vima.reservation.model;

import com.vima.gateway.ReservationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.UUID;


@Data
@Document("reservations")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservation {

    @Id
    UUID id;

    @Field
    int numOfGuests;

    @Field
    ReservationStatus status;

    @Field
    AccommodationInfo accomInfo;

    @Field
    DateRange desiredDate;

    @Field
    String userId;
}
