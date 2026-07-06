package com.izubot.treinemais.ui.new_training

import java.util.UUID

data class NewTrainingUiState(
    val trainingName: String = "",
    val exercises: List<ExerciseUiState> = emptyList(),
    val isSaving: Boolean = false,
    val message: String = ""
)

data class ExerciseUiState(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val sets: String = "",
    val reps: String = "",
    val weight: String = ""
)