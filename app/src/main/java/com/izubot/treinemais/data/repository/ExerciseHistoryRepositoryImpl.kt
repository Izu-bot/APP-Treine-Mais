package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.local.dao.ExerciseHistoryDao
import com.izubot.treinemais.data.local.dto.WeightEntry
import com.izubot.treinemais.data.mappers.toEntity
import com.izubot.treinemais.domain.model.ExerciseHistory
import com.izubot.treinemais.domain.repository.ExerciseHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExerciseHistoryRepositoryImpl @Inject constructor(
    private val exerciseHistoryDao: ExerciseHistoryDao
): ExerciseHistoryRepository {
    override suspend fun insertExerciseTraining(exerciseTraining: ExerciseHistory): Result<Unit> {
        return runCatching {
            exerciseHistoryDao.insertExerciseTraining(exerciseTraining.toEntity())
        }
    }

    override fun getWeightEvolution(exerciseId: String): Flow<List<WeightEntry>> {
        return exerciseHistoryDao.getWeightEvolution(exerciseId)
    }

    override suspend fun maxWeightByExercise(exerciseId: String): Double {
        return exerciseHistoryDao.maxWeightByExercise(exerciseId)
    }

    override suspend fun getLastExerciseVolume(exerciseId: String): Double {
        return exerciseHistoryDao.getLastExerciseVolume(exerciseId)
    }

    override suspend fun getExerciseVolumeRecord(exerciseId: String): Double {
        return exerciseHistoryDao.getExerciseVolumeRecord(exerciseId)
    }

    override suspend fun getTotalVolumeBetweenDates(startDate: String, endDate: String): Double {
        return exerciseHistoryDao.getTotalVolumeBetweenDates(startDate, endDate)
    }

    override suspend fun getLastTrainingVolume(trainingId: String): Double {
        return exerciseHistoryDao.getLastTrainingVolume(trainingId)
    }

    override suspend fun getTrainingVolumeRecord(trainingId: String): Double {
        return exerciseHistoryDao.getTrainingVolumeRecord(trainingId)
    }

    override fun getTrainingVolumeEvolution(trainingId: String): Flow<List<WeightEntry>> {
        return exerciseHistoryDao.getTrainingVolumeEvolution(trainingId)
    }
}