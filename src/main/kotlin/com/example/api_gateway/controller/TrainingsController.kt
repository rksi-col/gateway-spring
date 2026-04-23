package com.example.api_gateway.controller

import com.example.api_gateway.dto.AddExerciseRequest
import com.example.api_gateway.dto.AddExerciseResponse
import com.example.api_gateway.dto.CreateTrainingRequest
import com.example.api_gateway.dto.CreateTrainingResponse
import com.example.api_gateway.dto.ExerciseResponse
import com.example.api_gateway.dto.RemoveExerciseRequest
import com.example.api_gateway.dto.TrainingResponse
import jakarta.servlet.http.HttpServletRequest
import net.devh.boot.grpc.client.inject.GrpcClient
import org.example.proto.AddExerciseReq
import org.example.proto.CreateExercise
import org.example.proto.CreateTrainingReq
import org.example.proto.GetTrainingReq
import org.example.proto.GetTrainingResp
import org.example.proto.RemoveExerciseReq
import org.example.proto.TrainingsServiceGrpc
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.ZoneOffset

@RestController
@RequestMapping("/api/trainings")
class TrainingsController(
    @GrpcClient("trainings-service")
    private val stub: TrainingsServiceGrpc.TrainingsServiceBlockingStub
) {

    @PostMapping
    fun createTraining(
        request: HttpServletRequest,
        @RequestBody body: CreateTrainingRequest
    ): CreateTrainingResponse {
        val accountId = request.getAttribute("accountId") as Long

        val grpcRequest = CreateTrainingReq.newBuilder()
            .setAccountId(accountId)
            .setTimestamp(body.timestamp)
            .setCategory(body.category)
            .addAllExercises(body.exercises.map { ex ->
                CreateExercise.newBuilder()
                    .setExerciseId(ex.exerciseId)
                    .setSortId(ex.sortId)
                    .build()
            })
            .build()

        stub.createTraining(grpcRequest)
        return CreateTrainingResponse(status = "CREATED")
    }


    // ==================== ПОЛУЧЕНИЕ ====================

    /**
     * Получить тренировку по точному timestamp (миллисекунды)
     * GET /api/trainings/timestamp/1713123456789
     */
    @GetMapping("/timestamp/{timestamp}")
    fun getTrainingByTimestamp(
        request: HttpServletRequest,
        @PathVariable timestamp: Long
    ): TrainingResponse {
        val accountId = request.getAttribute("accountId") as Long

        val grpcRequest = GetTrainingReq.newBuilder()
            .setAccountId(accountId)
            .setTimestamp(timestamp)
            .build()

        val grpcResponse = stub.getTraining(grpcRequest)

        val training = grpcResponse.trainingsList.firstOrNull()
            ?: throw NoSuchElementException("Training not found for timestamp: $timestamp")

        return mapToTrainingResponse(training)
    }

    /**
     * Получить тренировки по диапазону timestamp (миллисекунды)
     * GET /api/trainings/range?from=1713123456789&to=1713209856789
     */
    @GetMapping("/range")
    fun getTrainingsByRange(
        request: HttpServletRequest,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): List<TrainingResponse> {
        val accountId = request.getAttribute("accountId") as Long

        val grpcRequest = GetTrainingReq.newBuilder()
            .setAccountId(accountId)
            .setRange(
                GetTrainingReq.TimestampRange.newBuilder()
                    .setFrom(from)
                    .setTo(to)
                    .build()
            )
            .build()

        val grpcResponse = stub.getTraining(grpcRequest)
        return grpcResponse.trainingsList.map { mapToTrainingResponse(it) }
    }

    /**
     * Получить тренировки за конкретный день (удобный эндпоинт)
     * GET /api/trainings/day?date=2026-04-23
     */
    @GetMapping("/day")
    fun getTrainingsByDay(
        request: HttpServletRequest,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): List<TrainingResponse> {
//        val accountId = request.getAttribute("accountId") as Long
        val accountIdAttr = request.getAttribute("accountId")
        println("🔍 accountId attribute: $accountIdAttr")  // ← Должен быть не null!

        val accountId = request.getAttribute("accountId") as Long

        val startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1

        return getTrainingsByRange(request, startOfDay, endOfDay)
    }

    /**
     * Получить тренировку по ID
     * GET /api/trainings/{id}
     */
    @GetMapping("/{id}")
    fun getTrainingById(@PathVariable id: Long): TrainingResponse {
        // TODO: добавить в микросервис метод GetTrainingById
        throw UnsupportedOperationException("GetTrainingById not implemented yet")
    }

    // ==================== УПРАВЛЕНИЕ УПРАЖНЕНИЯМИ ====================

    /**
     * Добавить упражнение в существующую тренировку
     * POST /api/trainings/exercises/add
     */
    @PostMapping("/exercises/add")
    @ResponseStatus(HttpStatus.CREATED)
    fun addExerciseToTraining(
        request: HttpServletRequest,
        @RequestBody body: AddExerciseRequest
    ): AddExerciseResponse {
        val accountId = request.getAttribute("accountId") as Long

        val grpcRequest = AddExerciseReq.newBuilder()
            .setAccountId(accountId)
            .setTrainingId(body.trainingId)
            .setExerciseId(body.exerciseId)
            .setSortId(body.sortId)
            .build()

        val grpcResponse = stub.addExerciseToTraining(grpcRequest)
        return AddExerciseResponse(exerciseId = grpcResponse.exerciseId)
    }

    /**
     * Удалить упражнение из тренировки
     * DELETE /api/trainings/exercises/remove
     */
    @DeleteMapping("/exercises/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeExerciseFromTraining(
        request: HttpServletRequest,
        @RequestBody body: RemoveExerciseRequest
    ) {
        val accountId = request.getAttribute("accountId") as Long

        val grpcRequest = RemoveExerciseReq.newBuilder()
            .setAccountId(accountId)
            .setTrainingId(body.trainingId)
            .setExerciseId(body.exerciseId)
            .build()

        stub.removeExerciseFromTraining(grpcRequest)
    }

    private fun mapToTrainingResponse(training: GetTrainingResp.Training): TrainingResponse {
        return TrainingResponse(
            id = training.id,
            timestamp = training.timestamp,
            category = training.category,
            exercises = training.exercisesList.map { ex ->
                ExerciseResponse(
                    id = ex.id,
                    sortId = ex.sortId,
                    name = ex.name,
                    targetMuscle = ex.targetMuscle
                )
            }
        )
    }
}