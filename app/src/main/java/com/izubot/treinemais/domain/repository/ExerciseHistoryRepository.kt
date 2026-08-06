package com.izubot.treinemais.domain.repository

import com.izubot.treinemais.data.local.dto.WeightEntry
import com.izubot.treinemais.domain.model.ExerciseHistory
import kotlinx.coroutines.flow.Flow

interface ExerciseHistoryRepository {
    suspend fun insertExerciseTraining(exerciseTraining: ExerciseHistory): Result<Unit>
    fun getWeightEvolution(exerciseId: String): Flow<List<WeightEntry>>
    suspend fun maxWeightByExercise(exerciseId: String): Double
    suspend fun getLastExerciseVolume(exerciseId: String): Double
    suspend fun getExerciseVolumeRecord(exerciseId: String): Double
    suspend fun getTotalVolumeBetweenDates(startDate: String, endDate: String): Double
    suspend fun getLastTrainingVolume(trainingId: String): Double
    suspend fun getTrainingVolumeRecord(trainingId: String): Double
    fun getTrainingVolumeEvolution(trainingId: String): Flow<List<WeightEntry>>
}