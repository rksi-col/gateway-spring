package com.example.api_gateway.config

//import org.example.protobuf.TrainingsServiceGrpc
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.grpc.client.GrpcChannelFactory
//
//@Configuration
//class GrpcClientConfig(
//    private val channelFactory: GrpcChannelFactory
//) {
//
//    @Bean
//    fun trainingsServiceStub(): TrainingsServiceGrpc.TrainingsServiceBlockingStub {
//        val channel = channelFactory.createChannel("trainings-service")
//        return TrainingsServiceGrpc.newBlockingStub(channel)
//    }
//}
import net.devh.boot.grpc.client.inject.GrpcClient
import net.devh.boot.grpc.client.inject.GrpcClientBean
import org.example.proto.TrainingsServiceGrpc
import org.springframework.context.annotation.Configuration

@Configuration
@GrpcClientBean(
    clazz = TrainingsServiceGrpc.TrainingsServiceBlockingStub::class,
    client = GrpcClient("trainings-service")
)
class GrpcClientConfig