# 🏋️ TrackWorkout — Фитнес-трекер

**Микросервисное приложение для отслеживания тренировок.**

## 🛠️ Стек

- **Язык:** Kotlin
- **Коммуникация:** gRPC (Protobuf)
- **Gateway:** Spring Boot 3
- **База данных:** PostgreSQL
- **Миграции:** Flyway
- **Аутентификация:** JWT

## 🚀 Запуск

### 1. Поднять PostgreSQL

docker-compose up -d

### 2. Запустить микросервисы

cd trainings-service && ./gradlew run
cd account-service && ./gradlew run
cd auth-service && ./gradlew run

### 3. Запустить Gateway

cd api-gateway && ./gradlew bootRun

Приложение доступно на http://localhost:8080
