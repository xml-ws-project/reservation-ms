package com.vima.reservation.grpc_service;

import com.vima.gateway.*;
import com.vima.reservation.dto.gRPCAccommodationObject;
import com.vima.reservation.dto.gRPCUserObject;
import com.vima.reservation.dto.gRPCObjectRec;
import com.vima.reservation.mapper.ReservationMapper;
import com.vima.reservation.service.ReservationService;

import communication.FindUserRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

@GrpcService
@RequiredArgsConstructor
public class ReservationGrpcService extends ReservationServiceGrpc.ReservationServiceImplBase {

    private final ReservationService service;
    @Value("${channel.address.auth-ms}")
    private String channelAuthAddress;
    @Value("${channel.address.accommodation-ms}")
    private String channelAccommodationAddress;
    @Value("${channel.address.recommendation-ms}")
    private String channelRecommendationAddress;

    @Override
    public void create(ReservationRequest request, StreamObserver<ReservationResponse> responseObserver){
        var accommodationBlockingStub = getBlockingAccommodationStub();
        var accom = accommodationBlockingStub.getStub()
                .findById(Uuid.newBuilder().setValue(request.getAccomId())
                .build());
        accommodationBlockingStub.getChannel().shutdown();

        var userBlockingStub = getBlockingUserStub();
        var host = userBlockingStub.getStub()
            .findById(FindUserRequest.newBuilder().setId(accom.getHostId())
                .build());
        userBlockingStub.getChannel().shutdown();

        var reservation = service.create(ReservationMapper.convertMessageToEntity(request, accom), accom, host);
        if(accom.getAutomaticAcceptance()) createNodeRelationship(reservation.getId().toString());
        responseObserver.onNext(ReservationMapper.convertEntityToMessage(reservation));
        responseObserver.onCompleted();
    }

    @Override
    public void findById(Uuid id, StreamObserver<ReservationResponse> responseObserver){
        var reservation = service.findById(UUID.fromString(id.getValue()));
        responseObserver.onNext(ReservationMapper.convertEntityToMessage(reservation));
        responseObserver.onCompleted();
    }

    @Override
    public void searchReservation(SearchReservationRequest request, StreamObserver<SearchReservationResponse> responseObserver) {
        var accommodationIds = service.search(request);
        var responseList = SearchReservationResponse.newBuilder()
            .addAllIds(accommodationIds)
            .build();
        responseObserver.onNext(responseList);
        responseObserver.onCompleted();
    }

    @Override
    public void findAll(Empty empty, StreamObserver<ReservationList> responseObserver){
        var response = ReservationMapper.convertEntityToMessageList(service.findAll());
        responseObserver.onNext(ReservationList.newBuilder().addAllReturnList(response).build());
        responseObserver.onCompleted();
    }

    @Override
    public void hostResponse(HostResponse response, StreamObserver<TextMessage> responseObserver){
        if(response.getAccept()) createNodeRelationship(response.getId());
        var reservation = service.findById(UUID.fromString(response.getId()));

        var userBlockingStub = getBlockingUserStub();
        var guest = userBlockingStub.getStub()
            .findById(FindUserRequest.newBuilder().setId(reservation.getUserId())
                .build());
        userBlockingStub.getChannel().shutdown();

        var accommodationBlockingStub = getBlockingAccommodationStub();
        var accom = accommodationBlockingStub.getStub()
            .findById(Uuid.newBuilder().setValue(reservation.getAccomInfo().getAccomId())
                .build());
        accommodationBlockingStub.getChannel().shutdown();

        var result = service.hostResponse(response, guest, accom);
        if(response.getAccept()) createNodeRelationship(response.getId());

        responseObserver.onNext(TextMessage.newBuilder().setValue(result).build());
        responseObserver.onCompleted();
    }

    @Override
    public void cancelReservation(Uuid id, StreamObserver<TextMessage> responseObserver){
        var reservation = service.findById(UUID.fromString(id.getValue()));

        var userBlockingStub = getBlockingUserStub();
        var host = userBlockingStub.getStub()
            .findById(FindUserRequest.newBuilder().setId(reservation.getAccomInfo().getHostId())
                .build());
        userBlockingStub.getChannel().shutdown();

        var accommodationBlockingStub = getBlockingAccommodationStub();
        var accom = accommodationBlockingStub.getStub()
            .findById(Uuid.newBuilder().setValue(reservation.getAccomInfo().getAccomId())
                .build());
        accommodationBlockingStub.getChannel().shutdown();

        var response = service.cancelReservation((UUID.fromString(id.getValue())), host, accom);
        responseObserver.onNext(TextMessage.newBuilder().setValue(response).build());
        responseObserver.onCompleted();
    }

    @Override
    public void findAllByUser(UserRequest request, StreamObserver<ReservationList> responseObserver){
        var response = ReservationMapper.convertEntityToMessageList(service.findAllByUser(request.getId(), request.getRole()));
        responseObserver.onNext(ReservationList.newBuilder().addAllReturnList(response).build());
        responseObserver.onCompleted();
    }

    @Override
    public void checkIfHostHasActiveReservations(CheckReservationsForUserRequest request, StreamObserver<CheckReservationsForUserResponse> responseObserver) {
        var response = service.checkIfHostHasActiveReservations(Long.toString(request.getId()));
        CheckReservationsForUserResponse responseCheck = CheckReservationsForUserResponse.newBuilder()
            .setContains(response)
            .build();
        responseObserver.onNext(responseCheck);
        responseObserver.onCompleted();
    }

    @Override
    public void checkIfGuestHasActiveReservations(CheckReservationsForUserRequest request, StreamObserver<CheckReservationsForUserResponse> responseObserver) {
        var response = service.checkIfGuestHasActiveReservations(Long.toString(request.getId()));
        CheckReservationsForUserResponse responseCheck = CheckReservationsForUserResponse.newBuilder()
            .setContains(response)
            .build();
        responseObserver.onNext(responseCheck);
        responseObserver.onCompleted();
    }

    private void createNodeRelationship(String reservationId){
        var res = service.findById(UUID.fromString(reservationId));
        ManagedChannel channel = ManagedChannelBuilder.forAddress(channelRecommendationAddress, 9095).usePlaintext().build();
        var object = gRPCObjectRec.builder().channel(channel).stub(RecommendationServiceGrpc.newBlockingStub(channel)).build();
        object.getStub().createReserveRel(RecommendationServiceOuterClass.ReserveRelationship.newBuilder().setUserId(res.getUserId()).setAccomId(res.getAccomInfo().getAccomId()).build());
        channel.shutdown();
    }

    private gRPCAccommodationObject getBlockingAccommodationStub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(channelAccommodationAddress, 9093)
            .usePlaintext()
            .build();
        return gRPCAccommodationObject.builder()
            .channel(channel)
            .stub(AccommodationServiceGrpc.newBlockingStub(channel))
            .build();
    }

    private gRPCUserObject getBlockingUserStub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(channelAuthAddress, 9092)
            .usePlaintext()
            .build();
        return gRPCUserObject.builder()
            .channel(channel)
            .stub(communication.userDetailsServiceGrpc.newBlockingStub(channel))
            .build();
    }
}
