package com.vima.reservation.dto;

import com.vima.gateway.AccommodationServiceGrpc;
import io.grpc.ManagedChannel;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class gRPCAccommodationObject {

    ManagedChannel channel;
    AccommodationServiceGrpc.AccommodationServiceBlockingStub stub;
}
