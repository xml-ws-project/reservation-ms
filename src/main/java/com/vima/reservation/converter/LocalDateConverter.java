package com.vima.reservation.converter;

import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class LocalDateConverter {

    public static Timestamp convertLocalDateToGoogleTimestamp(LocalDate localDate) {
        Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public static LocalDate convertGoogleTimeStampToLocalDate(Timestamp timestamp) {
        return Instant
                .ofEpochSecond(timestamp.getSeconds() , timestamp.getNanos())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
    }
}
