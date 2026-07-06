package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.local.dao.TrainingDao
import com.izubot.treinemais.data.mappers.toDomain
import com.izubot.treinemais.data.mappers.toEntity
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrainingRepositoryImpl @Inject constructor(
    private val trainingDao: TrainingDao
) : TrainingRepository {
    override suspend fun insertTraining(training: Training): Result<Unit> {
        return runCatching {
            val trainingEntity = training.toEntity()
            val exerciseEntities = training.exercises.map { exercise ->
                exercise.toEntity(trainingId = training.id)
            }
            trainingDao.insertTrainingWithExercises(trainingEntity, exerciseEntities)
        }
    }

    override fun getTraining(id: String): Flow<Training?> {
        return trainingDao.getTrainingsWithExercises(id).map { it?.toDomain() }
    }

    override suspend fun deleteTraining(training: Training): Result<Unit> {
        return runCatching {
            trainingDao.deleteTraining(training.toEntity())
        }
    }

    override suspend fun updateTraining(training: Training): Result<Unit> {
        return insertTraining(training) // Upsert logic via insertTrainingWithExercises
    }

    override fun getAllTrainings(): Flow<List<Training>> {
        return trainingDao.getTrainingsWithExercisesFlow().map { list ->
            list.map { it.toDomain() }
        }
    }
}
