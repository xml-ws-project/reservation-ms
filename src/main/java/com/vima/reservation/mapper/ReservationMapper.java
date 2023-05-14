package com.vima.reservation.mapper;

import com.vima.gateway.*;
import com.vima.reservation.converter.LocalDateConverter;
import com.vima.reservation.model.AccommodationInfo;
import com.vima.reservation.model.DateRange;
import com.vima.reservation.model.Reservation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ReservationMapper {

    public static Reservation convertMessageToEntity(ReservationRequest request, AccommodationResponse accom){
        var desiredDate = DateRange.builder()
                .start(LocalDateConverter.convertGoogleTimeStampToLocalDate(request.getDesiredDate().getStart()))
                .end(LocalDateConverter.convertGoogleTimeStampToLocalDate(request.getDesiredDate().getEnd()))
                .build();

        var accomInfo = AccommodationInfo.builder()
                .accomId(accom.getId())
                .minGuests(accom.getMinGuests())
                .maxGuests(accom.getMaxGuests())
                .city(accom.getCity())
                .country(accom.getCountry())
                .hostId(accom.getHostId())
                .build();

        return Reservation.builder()
                .id(UUID.randomUUID())
                .numOfGuests(request.getNumOfGuests())
                .status(ReservationStatus.PENDING)
                .accomInfo(accomInfo)
                .desiredDate(desiredDate)
                .userId(request.getUserId())
                .build();
    }

    public static ReservationResponse convertEntityToMessage(Reservation reservation){
       var accomInfo = com.vima.gateway.AccommodationInfo.newBuilder()
               .setAccomId(reservation.getAccomInfo().getAccomId())
               .setMinGuests(reservation.getAccomInfo().getMinGuests())
               .setMaxGuests(reservation.getAccomInfo().getMaxGuests())
               .setCity(reservation.getAccomInfo().getCity())
               .setCountry(reservation.getAccomInfo().getCountry())
               .setHostId(reservation.getAccomInfo().getHostId())
               .build();

       var desiredDate = com.vima.gateway.DateRange.newBuilder()
               .setStart(LocalDateConverter.convertLocalDateToGoogleTimestamp(reservation.getDesiredDate().getStart()))
               .setEnd(LocalDateConverter.convertLocalDateToGoogleTimestamp(reservation.getDesiredDate().getEnd()))
               .build();

        return  ReservationResponse.newBuilder()
                .setId(reservation.getId().toString())
                .setNumOfGuests(reservation.getNumOfGuests())
                .setStatus(reservation.getStatus())
                .setAccomInfo(accomInfo)
                .setDesiredDate(desiredDate)
                .setUserId(reservation.getUserId())
                .build();
    }

    public static List<ReservationResponse> convertEntityToMessageList(List<Reservation> httpList){
        List<ReservationResponse> grpcList = new ArrayList<>();
        httpList.forEach(item ->{
            grpcList.add(convertEntityToMessage(item));
        });

        return grpcList;
    }
}
