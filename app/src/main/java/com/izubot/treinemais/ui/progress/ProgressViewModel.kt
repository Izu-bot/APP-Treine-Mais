package com.izubot.treinemais.ui.progress

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.data.local.dto.WeightEntry
import com.izubot.treinemais.domain.model.DayProgress
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
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

    // Gerencia a observação ativa de fluxos (Flow) para evitar vazamento de corotinas
    private var observationJob: Job? = null

    // Armazena dados atuais para reprocessamento ao trocar granularidade
    private var currentHistory: List<DayProgress> = emptyList()
    private var currentTrainingEntries: List<WeightEntry> = emptyList()
    private var currentExerciseEntries: List<WeightEntry> = emptyList()

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
        observationJob?.cancel()
        observationJob = viewModelScope.launch {
            val now = LocalDate.now()
            val startOfLast6Months = now.minusMonths(5).withDayOfMonth(1)

            trainingHistoryRepository.getBetweenDates(
                startOfLast6Months.toString(),
                now.toString()
            ).collect { history ->
                currentHistory = history
                processFrequencyData(history)
                calculateMonthlyStats(history, now)
            }
        }
        
        viewModelScope.launch {
            val now = LocalDate.now()
            val startOfMonth = now.withDayOfMonth(1).toString()
            val endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).toString()
            val totalVolume =
                exerciseHistoryRepository.getTotalVolumeBetweenDates(startOfMonth, endOfMonth)
            _state.update { it.copy(totalMonthlyVolume = totalVolume) }
        }
    }

    private fun calculateMonthlyStats(history: List<DayProgress>, now: LocalDate) {
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

        val weeklyAvg = history.count { it.isCompleted }.toDouble() / 24.0

        _state.update {
            it.copy(
                monthlyWorkouts = currentMonthWorkouts,
                monthlyWorkoutsChange = currentMonthWorkouts - lastMonthWorkouts,
                weeklyAverage = weeklyAvg
            )
        }
    }

    private fun processFrequencyData(history: List<DayProgress>) {
        val now = LocalDate.now()
        val granularity = _state.value.chartGranularity

        if (granularity == ChartGranularity.MONTHLY) {
            val groupedByMonth = history.filter { it.isCompleted }
                .groupBy { "${it.date.year}-${String.format(Locale.US, "%02d", it.date.monthValue)}" }
                .mapValues { it.value.size.toDouble() }

            val monthlyData = mutableMapOf<String, Double>()
            for (i in 5 downTo 0) {
                val date = now.minusMonths(i.toLong())
                val monthKey = "${date.year}-${String.format(Locale.US, "%02d", date.monthValue)}"
                monthlyData[monthKey] = groupedByMonth[monthKey] ?: 0.0
            }
            updateChartState(monthlyData, ViewMode.GENERAL)
        } else {
            val weekFields = WeekFields.of(Locale.getDefault())
            val groupedByWeek = history.filter { it.isCompleted }
                .groupBy {
                    val week = it.date.get(weekFields.weekOfWeekBasedYear())
                    "${it.date.year}-W${String.format(Locale.US, "%02d", week)}"
                }
                .mapValues { it.value.size.toDouble() }

            val weeklyData = mutableMapOf<String, Double>()
            for (i in 9 downTo 0) { // Mostrar as últimas 10 semanas
                val date = now.minusWeeks(i.toLong())
                val week = date.get(weekFields.weekOfWeekBasedYear())
                val weekKey = "${date.year}-W${String.format(Locale.US, "%02d", week)}"
                weeklyData[weekKey] = groupedByWeek[weekKey] ?: 0.0
            }
            updateChartState(weeklyData, ViewMode.GENERAL)
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

        observationJob?.cancel()
        observationJob = viewModelScope.launch {
            val lastVol = exerciseHistoryRepository.getLastTrainingVolume(training.id)
            val recordVol = exerciseHistoryRepository.getTrainingVolumeRecord(training.id)
            _state.update {
                it.copy(
                    lastVolume = lastVol,
                    recordVolume = recordVol
                )
            }

            exerciseHistoryRepository.getTrainingVolumeEvolution(training.id).collect { entries ->
                currentTrainingEntries = entries
                processTrainingChartData(entries)
            }
        }
    }

    fun onGranularityChanged(granularity: ChartGranularity) {
        if (_state.value.chartGranularity == granularity) return
        
        _state.update { it.copy(chartGranularity = granularity) }
        refreshChartData()
    }

    private fun refreshChartData() {
        when (_state.value.viewMode) {
            ViewMode.GENERAL -> processFrequencyData(currentHistory)
            ViewMode.TRAINING -> processTrainingChartData(currentTrainingEntries)
            ViewMode.EXERCISE -> processExerciseChartData(currentExerciseEntries)
        }
    }

    fun onExerciseSelected(exercise: Exercise) {
        _state.update {
            it.copy(
                selectedExercise = exercise,
                isLoading = true,
                viewMode = ViewMode.EXERCISE
            )
        }

        viewModelScope.launch {
            val maxWeight = exerciseHistoryRepository.maxWeightByExercise(exercise.id)
            val lastVol = exerciseHistoryRepository.getLastExerciseVolume(exercise.id)
            val bestVol = exerciseHistoryRepository.getExerciseVolumeRecord(exercise.id)

            _state.update {
                it.copy(
                    recordWeight = maxWeight,
                    lastVolume = lastVol,
                    recordVolume = bestVol
                )
            }
        }

        loadExerciseHistory(exercise.id)
    }

    private fun loadExerciseHistory(exerciseId: String) {
        observationJob?.cancel()
        observationJob = viewModelScope.launch {
            exerciseHistoryRepository.getWeightEvolution(exerciseId).collect { entries ->
                val rm = entries.maxOfOrNull { Calculate1RM(it.weight, it.reps) } ?: 0.0

                currentExerciseEntries = entries
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

        val granularity = _state.value.chartGranularity
        val now = LocalDate.now()
        val chartData = if (granularity == ChartGranularity.MONTHLY) {
            entries
                .filter { it.date.length >= 7 }
                .groupBy { it.date.substring(0, 7) }
                .mapValues { group ->
                    group.value.maxOf { Calculate1RM(it.weight, it.reps) }
                }
        } else {
            val weekFields = WeekFields.of(Locale.getDefault())
            val allWeeklyData = entries
                .filter { it.date.length >= 10 }
                .groupBy { entry ->
                    val date = try {
                        LocalDate.parse(entry.date)
                    } catch (_: Exception) {
                        LocalDate.now()
                    }
                    val week = date.get(weekFields.weekOfWeekBasedYear())
                    "${date.year}-W${String.format(Locale.US, "%02d", week)}"
                }
                .mapValues { group ->
                    group.value.maxOf { Calculate1RM(it.weight, it.reps) }
                }

            val weeklyWindow = mutableMapOf<String, Double>()
            for (i in 9 downTo 0) {
                val date = now.minusWeeks(i.toLong())
                val week = date.get(weekFields.weekOfWeekBasedYear())
                val weekKey = "${date.year}-W${String.format(Locale.US, "%02d", week)}"
                weeklyWindow[weekKey] = allWeeklyData[weekKey] ?: 0.0
            }
            weeklyWindow
        }

        updateChartState(chartData.toSortedMap(), ViewMode.EXERCISE)
    }

    private fun processTrainingChartData(entries: List<WeightEntry>) {
        if (entries.isEmpty()) {
            _state.update { it.copy(chartPoints = emptyList(), chartLabels = emptyList()) }
            return
        }

        val granularity = _state.value.chartGranularity
        val now = LocalDate.now()
        val chartData = if (granularity == ChartGranularity.MONTHLY) {
            entries
                .filter { it.date.length >= 7 }
                .groupBy { it.date.substring(0, 7) }
                .mapValues { group -> group.value.sumOf { it.weight } }
        } else {
            val weekFields = WeekFields.of(Locale.getDefault())
            val allWeeklyData = entries
                .filter { it.date.length >= 10 }
                .groupBy { entry ->
                    val date = try {
                        LocalDate.parse(entry.date)
                    } catch (_: Exception) {
                        LocalDate.now()
                    }
                    val week = date.get(weekFields.weekOfWeekBasedYear())
                    "${date.year}-W${String.format(Locale.US, "%02d", week)}"
                }
                .mapValues { group -> group.value.sumOf { it.weight } }

            val weeklyWindow = mutableMapOf<String, Double>()
            for (i in 9 downTo 0) {
                val date = now.minusWeeks(i.toLong())
                val week = date.get(weekFields.weekOfWeekBasedYear())
                val weekKey = "${date.year}-W${String.format(Locale.US, "%02d", week)}"
                weeklyWindow[weekKey] = allWeeklyData[weekKey] ?: 0.0
            }
            weeklyWindow
        }

        updateChartState(chartData.toSortedMap(), ViewMode.TRAINING)
    }

    private fun updateChartState(data: Map<String, Double>, mode: ViewMode) {
        if (data.isEmpty()) {
            _state.update { it.copy(chartPoints = emptyList(), chartLabels = emptyList()) }
            return
        }

        val values = data.values.toList()
        val keys = data.keys.toList()
        val granularity = _state.value.chartGranularity

        // Labels (Nomes dos meses ou Semanas)
        val labels = keys.map { key ->
            if (granularity == ChartGranularity.MONTHLY) {
                val monthIndex = key.substringAfter("-").toIntOrNull() ?: 1
                monthNames.getOrElse(monthIndex - 1) { "???" }
            } else {
                // yyyy-Www -> Www
                key.substringAfter("-")
            }
        }

        // Normalização (0.0 a 1.0)
        val normalizedPoints = if (values.size == 1) {
            listOf(0.5f)
        } else {
            val max = values.maxOrNull() ?: 1.0
            val min = values.minOrNull() ?: 0.0
            val range = (max - min).coerceAtLeast(1.0)
            values.map { ((it - min) / range).toFloat() }
        }

        // Variação percentual (apenas para treinos e exercícios)
        val change = if (values.size >= 2) {
            val first = values.first()
            val last = values.last()
            if (first > 0) ((last - first) / first) * 100 else 0.0
        } else 0.0

        _state.update {
            it.copy(
                chartPoints = normalizedPoints,
                chartLabels = labels,
                viewMode = mode,
                percentageChange = change
            )
        }
    }

    fun resetState() {
        onTrainingSelected(null)
    }

    private fun observeSharedEvents() {
        viewModelScope.launch {
            uiEventManager.events.collect { event ->
                _channel.send(event)
            }
        }
    }
}
