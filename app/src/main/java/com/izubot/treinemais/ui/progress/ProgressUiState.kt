package com.izubot.treinemais.ui.progress

import com.izubot.treinemais.data.local.dto.WeightEntry
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.model.Training

data class ProgressUiState(
    val isLoading: Boolean = false,
    val selectedTraining: Training? = null,
    val selectedExercise: Exercise? = null,
    val allTrainings: List<Training> = emptyList(),
    val exercisesOfSelectedTraining: List<Exercise> = emptyList(),
    val weightEntries: List<WeightEntry> = emptyList(),
    val maxLoad: Double = 0.0,
    val chartPoints: List<Float> = emptyList(),
    val chartLabels: List<String> = emptyList(),
    val percentageChange: Double = 0.0,
    val monthlyWorkouts: Int = 0,
    val monthlyWorkoutsChange: Int = 0,
    val weeklyAverage: Double = 0.0,
    val viewMode: ViewMode = ViewMode.GENERAL,
    val lastVolume: Double = 0.0,
    val recordVolume: Double = 0.0,
    val recordWeight: Double = 0.0,
    val totalMonthlyVolume: Double = 0.0,
    val chartGranularity: ChartGranularity = ChartGranularity.WEEKLY,
)

enum class ViewMode {
    GENERAL, TRAINING, EXERCISE
}

enum class ChartGranularity {
    WEEKLY, MONTHLY
}
