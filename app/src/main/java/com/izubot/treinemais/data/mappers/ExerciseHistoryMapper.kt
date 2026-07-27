package com.izubot.treinemais.data.mappers

import com.izubot.treinemais.data.local.entities.ExerciseHistoryEntity
import com.izubot.treinemais.domain.model.ExerciseHistory
import java.time.LocalDate

fun ExerciseHistoryEntity.toDomain(date: String): ExerciseHistory {
    return ExerciseHistory(
        id = id,
        trainingHistoryId = trainingHistoryId,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        weight = weight,
        reps = reps,
        sets = sets,
        date = LocalDate.parse(date)
    )
}

fun ExerciseHistory.toEntity(): ExerciseHistoryEntity {
    return ExerciseHistoryEntity(
        id = id,
        trainingHistoryId = trainingHistoryId,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        weight = weight,
        reps = reps,
        sets = sets
    )
}