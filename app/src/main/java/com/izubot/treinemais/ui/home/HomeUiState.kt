package com.izubot.treinemais.ui.home

import com.izubot.treinemais.domain.model.DayProgress
import com.izubot.treinemais.domain.model.Training

data class HomeUiState (
    val greeting: String = "",
    val weeklyProgress: List<DayProgress> = emptyList(),
    val trainings: List<Training>? = null,
    val selectedTraining: Training? = null,
    val exerciseWeights: Map<String, List<String>> = emptyMap(),
    val isTrainingCompleted: Boolean = false
)
