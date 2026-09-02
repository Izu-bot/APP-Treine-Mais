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
                calculateWeeklyStats(history, now)
            }
        }
        
        viewModelScope.launch {
            val now = LocalDate.now()
            val startOfMonth = now.withDayOfMonth(1).toString()
            val endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).toString()
            val totalMonthVolume =
                exerciseHistoryRepository.getTotalVolumeBetweenDates(startOfMonth, endOfMonth)
            
            val weekFields = WeekFields.of(Locale.getDefault())
            val startOfWeek = now.with(weekFields.dayOfWeek(), 1).toString()
            val endOfWeek = now.with(weekFields.dayOfWeek(), 7).toString()
            val totalWeekVolume = 
                exerciseHistoryRepository.getTotalVolumeBetweenDates(startOfWeek, endOfWeek)
                
            _state.update { 
                it.copy(
                    totalMonthlyVolume = totalMonthVolume,
                    totalWeeklyVolume = totalWeekVolume
                ) 
            }
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

    private fun calculateWeeklyStats(history: List<DayProgress>, now: LocalDate) {
        val weekFields = WeekFields.of(Locale.getDefault())
        val weekBasedYearField = weekFields.weekBasedYear()
        
        val currentWeek = now.get(weekFields.weekOfWeekBasedYear())
        val currentWeekYear = now.get(weekBasedYearField)
        
        val lastWeekDate = now.minusWeeks(1)
        val lastWeek = lastWeekDate.get(weekFields.weekOfWeekBasedYear())
        val lastWeekYear = lastWeekDate.get(weekBasedYearField)
        
        val currentWeekWorkouts = history.count {
            it.isCompleted && 
            it.date.get(weekFields.weekOfWeekBasedYear()) == currentWeek && 
            it.date.get(weekBasedYearField) == currentWeekYear
        }
        val lastWeekWorkouts = history.count {
            it.isCompleted && 
            it.date.get(weekFields.weekOfWeekBasedYear()) == lastWeek && 
            it.date.get(weekBasedYearField) == lastWeekYear
        }

        _state.update {
            it.copy(
                weeklyWorkouts = currentWeekWorkouts,
                weeklyWorkoutsChange = currentWeekWorkouts - lastWeekWorkouts
            )
        }
    }

    private fun processFrequencyData(history: List<DayProgress>) {
        val chartData = generateChartData(
            items = history.filter { it.isCompleted },
            dateProvider = { it.date },
            valueAggregator = { it.size.toDouble() },
            granularity = _state.value.chartGranularity
        )
        updateChartState(chartData, ViewMode.GENERAL)
    }

    /**
     * Gera um Map de datas para valores para o gráfico, preenchendo lacunas com 0.0.
     * Esta lógica unifica a criação da janela temporal para as visualizações mensal (6 meses) e semanal (10 semanas).
     */
    private fun <T> generateChartData(
        items: List<T>,
        dateProvider: (T) -> LocalDate?,
        valueAggregator: (List<T>) -> Double,
        granularity: ChartGranularity
    ): Map<String, Double> {
        val now = LocalDate.now()
        val weekFields = WeekFields.of(Locale.getDefault())
        val weekBasedYearField = weekFields.weekBasedYear()

        // 1. Agrupa os dados existentes pela chave (Mês ou Semana)
        val groupedData = items.groupBy { item ->
            val date = dateProvider(item) ?: return@groupBy "INVALID"
            if (granularity == ChartGranularity.MONTHLY) {
                "${date.year}-${String.format(Locale.US, "%02d", date.monthValue)}"
            } else {
                val week = date.get(weekFields.weekOfWeekBasedYear())
                val year = date.get(weekBasedYearField)
                "$year-W${String.format(Locale.US, "%02d", week)}"
            }
        }.filterKeys { it != "INVALID" }
            .mapValues { valueAggregator(it.value) }

        // 2. Cria a janela temporal para garantir que o gráfico seja contínuo
        val result = mutableMapOf<String, Double>()
        val range = if (granularity == ChartGranularity.MONTHLY) 5 downTo 0 else 9 downTo 0

        for (i in range) {
            val date = if (granularity == ChartGranularity.MONTHLY)
                now.minusMonths(i.toLong())
            else
                now.minusWeeks(i.toLong())

            val key = if (granularity == ChartGranularity.MONTHLY) {
                "${date.year}-${String.format(Locale.US, "%02d", date.monthValue)}"
            } else {
                val week = date.get(weekFields.weekOfWeekBasedYear())
                val year = date.get(weekBasedYearField)
                "$year-W${String.format(Locale.US, "%02d", week)}"
            }
            result[key] = groupedData[key] ?: 0.0
        }

        return result
    }

    private fun parseDate(dateStr: String): LocalDate? {
        return try {
            if (dateStr.length >= 10) LocalDate.parse(dateStr)
            else if (dateStr.length >= 7) LocalDate.parse("$dateStr-01")
            else null
        } catch (_: Exception) {
            null
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

        val chartData = generateChartData(
            items = entries,
            dateProvider = { parseDate(it.date) },
            valueAggregator = { group -> group.maxOf { Calculate1RM(it.weight, it.reps) } },
            granularity = _state.value.chartGranularity
        )

        updateChartState(chartData, ViewMode.EXERCISE)
    }

    private fun processTrainingChartData(entries: List<WeightEntry>) {
        if (entries.isEmpty()) {
            _state.update { it.copy(chartPoints = emptyList(), chartLabels = emptyList()) }
            return
        }

        val chartData = generateChartData(
            items = entries,
            dateProvider = { parseDate(it.date) },
            valueAggregator = { group -> group.sumOf { it.weight } },
            granularity = _state.value.chartGranularity
        )

        updateChartState(chartData, ViewMode.TRAINING)
    }

    /**
     * Atualiza o estado da UI com os pontos do gráfico normalizados, labels e variação percentual.
     * 
     * [data]: Mapa ordenado de labels para valores.
     * [mode]: Modo de visualização atual (Geral, Treino ou Exercício).
     */
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

        // Normalização (0.0 a 1.0) para exibição no gráfico
        val normalizedPoints = if (values.size == 1) {
            listOf(0.5f)
        } else {
            val max = values.maxOrNull() ?: 1.0
            val min = values.minOrNull() ?: 0.0
            val range = (max - min).coerceAtLeast(1.0)
            values.map { ((it - min) / range).toFloat() }
        }

        // Cálculo da variação percentual
        // Buscamos o primeiro valor não-zero no período para evitar divisão por zero
        // e para mostrar uma progressão significativa se o usuário começou a treinar no meio do período.
        val change = if (values.size >= 2) {
            val last = values.last()
            val first = values.firstOrNull { it > 0 } ?: 0.0
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
