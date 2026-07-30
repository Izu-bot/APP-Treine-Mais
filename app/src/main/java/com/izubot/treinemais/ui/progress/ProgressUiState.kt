package com.izubot.treinemais.ui.progress

import com.izubot.treinemais.data.local.dto.WeightEntry
import com.izubot.treinemais.domain.model.Exercise

data class ProgressUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilters: Set<String> = emptySet(),
    val selectedExercise: Exercise? = null,
    val allExercises: List<Exercise> = emptyList(),
    val filteredExercises: List<Exercise> = emptyList(),
    val weightEntries: List<WeightEntry> = emptyList(),
    val isExerciseSelected: Boolean = false
)