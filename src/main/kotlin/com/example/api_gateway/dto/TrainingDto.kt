package com.example.api_gateway.dto


data class CreateTrainingRequest(
    val timestamp: Long,
    val category: String,
    var exercises: MutableList<AddExerciseRequest> = mutableListOf(),
)

data class CreateExerciseRequest(
    val sortId: Long,
    val name: String,
    val targetMuscle: String
)

data class AddExerciseRequest(
    val trainingId: Long,
    val exerciseId: Long,
    val sortId: Long
)

data class RemoveExerciseRequest(
    val trainingId: Long,
    val exerciseId: Long
)

////////////////////////// Ответы
data class CreateTrainingResponse(
    val status: String = "CREATED"
)

data class TrainingResponse(
    val id: Long,
    val timestamp: Long,
    val category: String,
    val exercises: List<ExerciseResponse>
)

data class ExerciseResponse(
    val id: Long,
    val sortId: Long,
    val name: String,
    val targetMuscle: String
)

data class AddExerciseResponse(
    val exerciseId: Long
)

data class ErrorResponse(
    val error: String,
    val message: String,
    val status: Int
)