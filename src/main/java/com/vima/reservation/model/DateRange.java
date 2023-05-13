package com.vima.reservation.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DateRange {

    @Field
    LocalDate start;

    @Field
    LocalDate end;

    public DateRange(LocalDate start,LocalDate end){
        if(start.isAfter(end))
            throw new IllegalStateException();

        this.start = start;
        this.end = end;
    }
}
