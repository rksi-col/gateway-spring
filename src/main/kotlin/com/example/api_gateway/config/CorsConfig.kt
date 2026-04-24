package com.example.api_gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {

    @Bean
    fun corsFilter(): CorsFilter {
        val config = CorsConfiguration().apply {
            // Разрешаем запросы с фронта
            allowedOrigins = listOf(
                "http://localhost:5173",   // Vite
                "http://localhost:3000"    // Create React App
            )

            // Разрешаем все HTTP методы
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")

            // Разрешаем все заголовки
            allowedHeaders = listOf("*")

            // Разрешаем передавать токен в Authorization
            allowCredentials = false  // ← false, потому что allowedOrigins не "*"

            // Можно также разрешить все origins (для разработки)
            // addAllowedOriginPattern("*")  // ← Вместо allowedOrigins
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)  // ← Все эндпоинты
        }

        return CorsFilter(source)
    }
}