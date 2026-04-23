package com.example.api_gateway.config

import io.grpc.StatusRuntimeException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.devh.boot.grpc.client.inject.GrpcClient
import org.example.proto.AuthServiceGrpc
import org.example.proto.ValidateTokenReq
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor(
    @GrpcClient("auth-service")
    private val authStub: AuthServiceGrpc.AuthServiceBlockingStub
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        // Пропускаем эндпоинты авторизации
        if (request.requestURI.startsWith("/api/auth/")) {
            return true
        }

        val authHeader = request.getHeader("Authorization")
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header")

        if (!authHeader.startsWith("Bearer ")) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Authorization header")
        }

        val token = authHeader.substring(7)

        try {
            val validation = authStub.validateToken(
                ValidateTokenReq.newBuilder().setToken(token).build()
            )

            if (!validation.valid) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token")
            }

            // Прокидываем accountId в контроллер
            request.setAttribute("accountId", validation.accountId)

        } catch (e: StatusRuntimeException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed")
        }

        return true
    }
}