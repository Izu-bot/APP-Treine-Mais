package com.izubot.treinemais.domain.repository

import com.izubot.treinemais.data.local.dto.WeightEntry
import com.izubot.treinemais.domain.model.ExerciseHistory
import kotlinx.coroutines.flow.Flow

interface ExerciseHistoryRepository {
    suspend fun insertExerciseTraining(exerciseTraining: ExerciseHistory): Result<Unit>
    fun getWeightEvolution(exerciseId: String): Flow<List<WeightEntry>>
}