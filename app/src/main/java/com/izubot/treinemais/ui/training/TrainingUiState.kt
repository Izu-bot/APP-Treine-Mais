package com.izubot.treinemais.ui.training

import com.izubot.treinemais.domain.model.Training

data class TrainingUiState (
    val isLoading: Boolean = false,
    val trainings: List<Training> = emptyList(),
    val expandedCardIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
    val messageTrainingsEmpty: String? = null,
    val isListEmpty: Boolean = true
)
