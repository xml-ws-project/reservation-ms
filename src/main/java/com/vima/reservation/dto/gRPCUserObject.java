package com.vima.reservation.dto;

import com.vima.gateway.AccommodationServiceGrpc;

import io.grpc.ManagedChannel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import communication.userDetailsServiceGrpc;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class gRPCUserObject {

	ManagedChannel channel;
	userDetailsServiceGrpc.userDetailsServiceBlockingStub stub;
}
