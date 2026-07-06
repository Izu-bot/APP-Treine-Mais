package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.local.dao.ExerciseDao
import com.izubot.treinemais.data.mappers.toDomain
import com.izubot.treinemais.data.mappers.toEntity
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.repository.ExerciseRepository
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {
    suspend fun insertExerciseWithTrainingId(exercise: Exercise, trainingId: String): Result<Unit> {
        return runCatching {
            exerciseDao.insertExercise(exercise.toEntity(trainingId))
        }
    }

    override suspend fun getExercise(id: String): Result<Exercise?> {
        return runCatching {
            exerciseDao.getExerciseWithId(id)?.toDomain()
        }
    }

    override suspend fun getExerciseName(name: String): Result<Exercise?> {
        return runCatching {
            exerciseDao.getExerciseWithName(name)?.toDomain()
        }
    }

    override suspend fun deleteExercise(exercise: Exercise): Result<Unit> {
        return runCatching {
            val existing = exerciseDao.getExerciseWithId(exercise.id)
            if (existing != null) {
                exerciseDao.deleteExercise(existing)
            }
        }
    }

    override suspend fun updateExercise(exercise: Exercise): Result<Unit> {
        return runCatching {
            val existing = exerciseDao.getExerciseWithId(exercise.id)
            if (existing != null) {
                exerciseDao.upsertExercise(exercise.toEntity(existing.trainingId))
            } else {
                throw Exception("Exercício não encontrado para atualização.")
            }
        }
    }

    override suspend fun getAllExercise(): Result<List<Exercise>> {
        return runCatching {
            exerciseDao.getAllExercises().map { it.toDomain() }
        }
    }
}
