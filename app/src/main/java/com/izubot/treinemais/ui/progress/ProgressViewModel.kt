package com.izubot.treinemais.ui.progress

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.data.local.dto.WeightEntry
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.domain.repository.ExerciseHistoryRepository
import com.izubot.treinemais.domain.repository.TrainingHistoryRepository
import com.izubot.treinemais.domain.repository.TrainingRepository
import com.izubot.treinemais.utils.Calculate1RM
import com.izubot.treinemais.utils.UiEvent
import com.izubot.treinemais.utils.UiEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val trainingRepository: TrainingRepository,
    private val trainingHistoryRepository: TrainingHistoryRepository,
    private val exerciseHistoryRepository: ExerciseHistoryRepository,
    private val uiEventManager: UiEventManager
) : ViewModel() {
    private val _state = MutableStateFlow(ProgressUiState())
    val state = _state.asStateFlow()

    private val _channel = Channel<UiEvent>()
    val channel = _channel.receiveAsFlow()

    private val monthNames by lazy {
        context.resources.getStringArray(R.array.month_names_short).toList()
    }

    init {
        loadInitialData()
        observeSharedEvents()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            trainingRepository.getAllTrainings().collect { trainings ->
                _state.update { it.copy(allTrainings = trainings) }
            }
        }
        loadGeneralStats()
    }

    private fun loadGeneralStats() {
        viewModelScope.launch {
            val now = java.time.LocalDate.now()
            val startOfLast6Months = now.minusMonths(5).withDayOfMonth(1)

            trainingHistoryRepository.getBetweenDates(
                startOfLast6Months.toString(),
                now.toString()
            ).collect { history ->
                processFrequencyData(history)
                calculateMonthlyStats(history, now)
            }
        }
    }

    private fun calculateMonthlyStats(
        history: List<com.izubot.treinemais.domain.model.DayProgress>,
        now: java.time.LocalDate
    ) {
        val currentMonth = now.monthValue
        val lastMonth = now.minusMonths(1).monthValue
        val year = now.year
        val lastMonthYear = now.minusMonths(1).year

        val currentMonthWorkouts = history.count {
            it.isCompleted && it.date.monthValue == currentMonth && it.date.year == year
        }
        val lastMonthWorkouts = history.count {
            it.isCompleted && it.date.monthValue == lastMonth && it.date.year == lastMonthYear
        }

        val weeklyAvg =
            history.count { it.isCompleted }.toDouble() / 24.0

        _state.update {
            it.copy(
                monthlyWorkouts = currentMonthWorkouts,
                monthlyWorkoutsChange = currentMonthWorkouts - lastMonthWorkouts,
                weeklyAverage = weeklyAvg
            )
        }
    }

    private fun processFrequencyData(history: List<com.izubot.treinemais.domain.model.DayProgress>) {
        val groupedByMonth = history.filter { it.isCompleted }
            .groupBy { it.date.monthValue }
            .mapValues { it.value.size }

        val now = java.time.LocalDate.now()
        val labels = mutableListOf<String>()
        val points = mutableListOf<Float>()

        for (i in 5 downTo 0) {
            val date = now.minusMonths(i.toLong())
            val month = date.monthValue
            labels.add(monthNames[month - 1])
            val count = groupedByMonth[month] ?: 0
            points.add(count.toFloat())
        }

        val max = points.maxOrNull()?.coerceAtLeast(1f) ?: 1f
        val normalizedPoints = points.map { it / max }

        _state.update {
            it.copy(
                chartPoints = normalizedPoints,
                chartLabels = labels,
                viewMode = ViewMode.GENERAL,
                percentageChange = 0.0 // Poderia calcular a variação de frequência
            )
        }
    }

    fun onTrainingSelected(training: Training?) {
        if (training == null) {
            loadGeneralStats()
            _state.update {
                it.copy(
                    selectedTraining = null,
                    selectedExercise = null,
                    exercisesOfSelectedTraining = emptyList(),
                    viewMode = ViewMode.GENERAL
                )
            }
            return
        }

        _state.update {
            it.copy(
                selectedTraining = training,
                selectedExercise = null,
                exercisesOfSelectedTraining = training.exercises,
                viewMode = ViewMode.TRAINING
            )
        }
        // Para o modo de treinamento, poderíamos mostrar o volume desse treinamento
        // Mas, por enquanto, vamos apenas mostrar a seleção de exercícios
    }

    fun onExerciseSelected(exercise: Exercise) {
        _state.update {
            it.copy(
                selectedExercise = exercise,
                isLoading = true,
                viewMode = ViewMode.EXERCISE
            )
        }
        loadExerciseHistory(exercise.id)
    }

    private fun loadExerciseHistory(exerciseId: String) {
        viewModelScope.launch {
            exerciseHistoryRepository.getWeightEvolution(exerciseId).collect { entries ->
                val rm = entries.maxOfOrNull { Calculate1RM(it.weight, it.reps) } ?: 0.0

                _state.update {
                    it.copy(
                        weightEntries = entries,
                        isLoading = false,
                        maxLoad = rm
                    )
                }

                processExerciseChartData(entries)
            }
        }
    }

    private fun processExerciseChartData(entries: List<WeightEntry>) {
        if (entries.isEmpty()) {
            _state.update { it.copy(chartPoints = emptyList(), chartLabels = emptyList()) }
            return
        }

        val sortedMap = entries.map { entry ->
            val weight1RM = Calculate1RM(entry.weight, entry.reps)
            val month = entry.date.substring(5, 7)
            month to weight1RM
        }
            .groupBy { it.first }
            .mapValues { group -> group.value.maxOf { it.second } }
            .toSortedMap()

        val monthlyValues = sortedMap.values.toList()
        val monthKeys = sortedMap.keys.toList()

        val labels = monthKeys.map { monthStr ->
            val monthIndex = monthStr.toIntOrNull() ?: 1
            monthNames.getOrElse(monthIndex - 1) { "???" }
        }

        val maxWeight = monthlyValues.maxOrNull() ?: 1.0
        val minWeight = monthlyValues.minOrNull() ?: 0.0
        val range = (maxWeight - minWeight).coerceAtLeast(1.0)

        val normalizedPoints = monthlyValues.map { weight ->
            ((weight - minWeight) / range).toFloat()
        }

        val first = monthlyValues.first()
        val last = monthlyValues.last()
        val change = ((last - first) / first) * 100

        _state.update {
            it.copy(
                chartPoints = normalizedPoints,
                chartLabels = labels,
                percentageChange = change,
            )
        }
    }

    private fun observeSharedEvents() {
        viewModelScope.launch {
            uiEventManager.events.collect { event ->
                _channel.send(event)
            }
        }
    }
}
