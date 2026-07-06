package com.izubot.treinemais.domain.repository

import com.izubot.treinemais.domain.model.Training
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    suspend fun insertTraining(training: Training): Result<Unit>
    fun getTraining(id: String): Flow<Training?>
    suspend fun deleteTraining(training: Training): Result<Unit>
    suspend fun updateTraining(training: Training): Result<Unit>
    fun getAllTrainings(): Flow<List<Training>>
}
