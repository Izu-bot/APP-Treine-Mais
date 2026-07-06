package com.izubot.treinemais.data.mappers

import com.izubot.treinemais.data.local.entities.TrainingEntity
import com.izubot.treinemais.data.local.entities.TrainingWithExercise
import com.izubot.treinemais.domain.model.Training


fun TrainingWithExercise.toDomain(): Training {
    return Training(
        id = this.training.id,
        title = this.training.name,
        exercises = this.exercise.map { it.toDomain() }
    )
}

fun Training.toEntity(): TrainingEntity {
    return TrainingEntity(
        id = this.id,
        name = this.title,
        description = null
    )
}