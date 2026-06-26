package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.local.dao.ExerciseDao
import com.izubot.treinemais.data.mappers.toDomain
import com.izubot.treinemais.data.mappers.toEntity
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.repository.ExerciseRepository
import jakarta.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {
    override suspend fun insertExercise(exercise: Exercise): Result<Unit> {
        return Result.failure(Exception("Exercícios não devem ser criados isoladamente. Use o TrainingRepository."))
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
            exerciseDao.deleteExercise(exercise.toEntity(trainingId = ""))
        }
    }

    override suspend fun updateExercise(exercise: Exercise): Result<Unit> {
        return Result.failure(Exception("Exercícios não devem ser atualizados isoladamente. Use o TrainingRepository."))
    }

    override suspend fun getAllExercise(): Result<List<Exercise>> {
        return runCatching {
            exerciseDao.getAllExercises().map { it.toDomain() }
        }
    }
}
