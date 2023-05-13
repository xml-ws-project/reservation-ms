package com.vima.reservation.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DateRange {
    LocalDate start;
    LocalDate end;

    public DateRange(LocalDate start,LocalDate end){
        if(start.isAfter(end))
            throw new IllegalStateException();

        this.start = start;
        this.end = end;
    }
}
