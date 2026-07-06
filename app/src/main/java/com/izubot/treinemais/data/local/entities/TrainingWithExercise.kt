package com.izubot.treinemais.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TrainingWithExercise(
    @Embedded val training: TrainingEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "training_id"
    )
    val exercise: List<ExerciseEntity>
)
