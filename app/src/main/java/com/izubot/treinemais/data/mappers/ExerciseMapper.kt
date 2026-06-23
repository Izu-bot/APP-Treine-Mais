package com.izubot.treinemais.data.mappers

import com.izubot.treinemais.data.local.entities.ExerciseEntity
import com.izubot.treinemais.domain.model.Exercise

fun ExerciseEntity.toDomain() : Exercise {
    return Exercise(
        id = id,
        name = name,
        sets = sets.toString(),
        reps = reps.toString(),
        description = description ?: ""
    )
}

fun Exercise.toEntity() : ExerciseEntity {
    return ExerciseEntity(
        id = id,
        name = name,
        sets = sets?.toIntOrNull() ?: 0,
        reps = reps?.toIntOrNull() ?: 0,
        description = description
    )
}