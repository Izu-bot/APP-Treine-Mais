package com.izubot.treinemais.ui.training_log

data class TrainingLogUiState(
    val teste: String? = null,
    val exerciseWeights: Map<String, List<String>> = emptyMap(),
    val isTrainingCompleted: Boolean = false,
    val confirmedExerciseIds: Set<String> = emptySet(),
)
