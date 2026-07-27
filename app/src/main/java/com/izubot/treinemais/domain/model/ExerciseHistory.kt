package com.izubot.treinemais.domain.model

import java.time.LocalDate

data class ExerciseHistory(
    val id: Long = 0,
    val trainingHistoryId: Long,
    val exerciseId: String,
    val exerciseName: String,
    val weight: Double,
    val reps: Int,
    val sets: Int,
    val date: LocalDate
)
