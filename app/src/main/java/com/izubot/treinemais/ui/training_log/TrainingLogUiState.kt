package com.izubot.treinemais.ui.training_log

import com.izubot.treinemais.domain.model.Training

data class TrainingLogUiState(
    val training: Training? = null,
    val exerciseSet: Map<String, List<ExerciseSetLog>> = emptyMap(),
    val isTrainingCompleted: Boolean = false,
    val confirmedExerciseIds: Set<String> = emptySet(),
    val openDialog: Boolean = false,
    val isSaving: Boolean = false,
    val showTrophy: Boolean = false,
)

data class ExerciseSetLog(
    val reps: String = "",
    val weight: String = "",
    val isCompleted: Boolean = false
)