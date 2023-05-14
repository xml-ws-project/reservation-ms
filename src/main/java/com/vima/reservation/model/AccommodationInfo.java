package com.vima.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationInfo {

    @Field
    String accomId;

    @Field
    int minGuests;

    @Field
    int maxGuests;

    @Field
    String city;

    @Field
    String country;

    @Field
    String hostId;
}
