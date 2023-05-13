package com.vima.reservation.service.impl;

import com.vima.gateway.ReservationRequest;
import com.vima.gateway.ReservationStatus;
import com.vima.gateway.SearchRequest;
import com.vima.gateway.SearchReservationRequest;
import com.vima.reservation.converter.LocalDateConverter;
import com.vima.reservation.model.Reservation;
import com.vima.reservation.repository.ReservationRepository;
import com.vima.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;

import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository repository;

    @Override
    public Reservation create(Reservation newReservation) {
       return repository.save(newReservation);
    }

    @Override
    public Reservation findById(UUID id) {
        return null;
    }

    @Override
    public List<String> search(final SearchReservationRequest request) {
        LocalDate periodStart = LocalDateConverter.convertGoogleTimeStampToLocalDate(request.getPeriod().getStart());
        LocalDate periodEnd = LocalDateConverter.convertGoogleTimeStampToLocalDate(request.getPeriod().getEnd());
        List<Reservation> resultList = repository.searchReservations(ReservationStatus.ACCEPTED, request.getCountry(), request.getCity(), request.getGuests(), periodStart, periodEnd);
        List<String> accommodationIds = new ArrayList<>();
        resultList.forEach(reservation -> {
            accommodationIds.add(reservation.getAccomInfo().getAccomId());
        });
        return accommodationIds;
    }
}
