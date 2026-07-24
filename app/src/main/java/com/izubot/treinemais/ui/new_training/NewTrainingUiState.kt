package com.izubot.treinemais.ui.new_training

import java.util.UUID

data class NewTrainingUiState(
    val trainingName: String = "",
    val trainingNameError: Boolean = false,
    val exercises: List<ExerciseUiState> = emptyList(),
    val isSaving: Boolean = false,
    val message: Int = 0,
    val error: Boolean = false
)

data class ExerciseUiState(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val sets: String = "",
    val reps: String = "",
    val weight: String = "",
    val error: Boolean = false,
    val message: Int = 0
)