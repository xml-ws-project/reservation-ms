package com.vima.reservation.grpc_service;

import com.vima.gateway.*;
import com.vima.reservation.dto.gRPCObject;
import com.vima.reservation.mapper.ReservationMapper;
import com.vima.reservation.service.ReservationService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class ReservationGrpcService extends ReservationServiceGrpc.ReservationServiceImplBase {

    private final ReservationService service;

    @Override
    public void create(ReservationRequest request, StreamObserver<ReservationResponse> responseObserver){
        var accom = getBlockingStub().getStub()
                .findById(Uuid.newBuilder().setValue(request.getAccomId())
                .build());
        getBlockingStub().getChannel().shutdown();

        var reservation = service.create(ReservationMapper.convertMessageToEntity(request, accom));
        responseObserver.onNext(ReservationMapper.convertEntityToMessage(reservation));
        responseObserver.onCompleted();
    }

    @Override
    public void findById(Uuid id, StreamObserver<ReservationResponse> responseObserver){
        var reservation = service.findById(UUID.fromString(id.getValue()));
        responseObserver.onNext(ReservationMapper.convertEntityToMessage(reservation));
        responseObserver.onCompleted();
    }

    private gRPCObject getBlockingStub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9093)
                .usePlaintext()
                .build();
        return gRPCObject.builder()
                .channel(channel)
                .stub(AccommodationServiceGrpc.newBlockingStub(channel))
                .build();
    }
}
