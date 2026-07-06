package com.izubot.treinemais.domain.repository

import com.izubot.treinemais.domain.model.Exercise

interface ExerciseRepository {
    suspend fun getExercise(id: String) : Result<Exercise?>
    suspend fun getExerciseName(name: String) : Result<Exercise?>
    suspend fun deleteExercise(exercise: Exercise) : Result<Unit>
    suspend fun updateExercise(exercise: Exercise) : Result<Unit>
    suspend fun getAllExercise() : Result<List<Exercise>>
}