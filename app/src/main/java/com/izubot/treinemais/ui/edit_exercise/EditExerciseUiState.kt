package com.izubot.treinemais.ui.edit_exercise

data class EditExerciseUiState(
    val id: String = "",
    val name: String = "",
    val sets: String = "",
    val reps: String = "",
    val weight: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)