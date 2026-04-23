package com.example.api_gateway.controller

import net.devh.boot.grpc.client.inject.GrpcClient
import org.example.proto.AccountServiceGrpc
import org.example.proto.AuthServiceGrpc
import org.example.proto.GenerateTokenReq
import org.example.proto.LoginReq
import org.example.proto.RegisterReq
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    @GrpcClient("account-service")
    private val accountStub: AccountServiceGrpc.AccountServiceBlockingStub,
    @GrpcClient("auth-service")
    private val authStub: AuthServiceGrpc.AuthServiceBlockingStub
) {

    @PostMapping("/register")
    fun register(@RequestBody request: AuthRequest): AuthResponse {
        val accountResp = accountStub.register(
            RegisterReq.newBuilder()
                .setUsername(request.username)
                .setPassword(request.password)
                .build()
        )

        val tokenResp = authStub.generateToken(
            GenerateTokenReq.newBuilder()
                .setAccountId(accountResp.accountId)
                .build()
        )

        return AuthResponse(
            token = tokenResp.token,
            accountId = accountResp.accountId,
            username = request.username
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): AuthResponse {
        val accountResp = accountStub.login(
            LoginReq.newBuilder()
                .setUsername(request.username)
                .setPassword(request.password)
                .build()
        )

        val tokenResp = authStub.generateToken(
            GenerateTokenReq.newBuilder()
                .setAccountId(accountResp.accountId)
                .build()
        )

        return AuthResponse(
            token = tokenResp.token,
            accountId = accountResp.accountId,
            username = request.username
        )
    }
}

data class AuthRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val accountId: Long,
    val username: String
)