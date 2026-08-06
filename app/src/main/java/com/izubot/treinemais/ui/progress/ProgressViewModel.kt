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
        viewModelScope.launch {
            val now = java.time.LocalDate.now()
            val startOfMonth = now.withDayOfMonth(1).toString()
            val endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).toString()
            val totalVolume =
                exerciseHistoryRepository.getTotalVolumeBetweenDates(startOfMonth, endOfMonth)
            _state.update { it.copy(totalMonthlyVolume = totalVolume) }
        }
    }

    private fun calculateMonthlyStats(
        history: List<DayProgress>,
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

    private fun processFrequencyData(history: List<DayProgress>) {
        val now = java.time.LocalDate.now()

        // Agrupa por Ano-Mês para evitar conflitos entre anos (ex: Dez/24 e Jan/25)
        val groupedByMonth = history.filter { it.isCompleted }
            .groupBy { "${it.date.year}-${String.format(java.util.Locale.US, "%02d", it.date.monthValue)}" }
            .mapValues { it.value.size }

        val labels = mutableListOf<String>()
        val points = mutableListOf<Float>()

        // Garante a exibição dos últimos 6 meses em ordem cronológica
        for (i in 5 downTo 0) {
            val date = now.minusMonths(i.toLong())
            val monthKey = "${date.year}-${String.format(java.util.Locale.US, "%02d", date.monthValue)}"

            labels.add(monthNames[date.monthValue - 1])
            val count = groupedByMonth[monthKey] ?: 0
            points.add(count.toFloat())
        }

        val max = points.maxOrNull()?.coerceAtLeast(1f) ?: 1f
        val normalizedPoints = points.map { it / max }

        _state.update {
            it.copy(
                chartPoints = normalizedPoints,
                chartLabels = labels,
                viewMode = ViewMode.GENERAL,
                percentageChange = 0.0
            )
        }
    }

    fun onTrainingSelected(training: Training?) {
        if (training == null) {
            // Quando nada está selecionado, mostramos a frequência geral do usuário
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

        // Quando um treino é selecionado, mudamos o modo de visualização
        _state.update {
            it.copy(
                selectedTraining = training,
                selectedExercise = null,
                exercisesOfSelectedTraining = training.exercises,
                viewMode = ViewMode.TRAINING
            )
        }

        viewModelScope.launch {
            // Carregamos os dados de volume (último e recorde) para os cards de estatísticas
            val lastVol = exerciseHistoryRepository.getLastTrainingVolume(training.id)
            val recordVol = exerciseHistoryRepository.getTrainingVolumeRecord(training.id)
            _state.update {
                it.copy(
                    lastVolume = lastVol,
                    recordVolume = recordVol
                )
            }

            // Buscamos a evolução do volume total deste treino para o gráfico
            exerciseHistoryRepository.getTrainingVolumeEvolution(training.id).collect { entries ->
                processTrainingChartData(entries)
            }
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

        // Mapeia e agrupa por Ano-Mês (YYYY-MM) para garantir a ordenação correta
        val sortedMap = entries
            .filter { it.date.length >= 7 }
            .map { entry ->
                val weight1RM = Calculate1RM(entry.weight, entry.reps)
                val yearMonth = entry.date.substring(0, 7) // Ex: "2026-08"
                yearMonth to weight1RM
            }
            .groupBy { it.first }
            .mapValues { group -> group.value.maxOf { it.second } }
            .toSortedMap()

        if (sortedMap.isEmpty()) {
            _state.update { it.copy(chartPoints = emptyList(), chartLabels = emptyList()) }
            return
        }

        val monthlyValues = sortedMap.values.toList()
        val monthKeys = sortedMap.keys.toList()

        // Extrai o mês da chave YYYY-MM para pegar o nome
        val labels = monthKeys.map { key ->
            val monthIndex = key.substringAfter("-").toIntOrNull() ?: 1
            monthNames.getOrElse(monthIndex - 1) { "???" }
        }

        // Se houver apenas 1 ponto, centralizamos ele verticalmente (0.5f)
        val normalizedPoints = if (monthlyValues.size == 1) {
            listOf(0.5f)
        } else {
            val maxWeight = monthlyValues.maxOrNull() ?: 1.0
            val minWeight = monthlyValues.minOrNull() ?: 0.0
            val range = (maxWeight - minWeight).coerceAtLeast(1.0)
            monthlyValues.map { weight -> ((weight - minWeight) / range).toFloat() }
        }

        val first = monthlyValues.first()
        val last = monthlyValues.last()
        val change = if (first > 0) ((last - first) / first) * 100 else 0.0

        _state.update {
            it.copy(
                chartPoints = normalizedPoints,
                chartLabels = labels,
                percentageChange = change,
            )
        }
    }

    private fun processTrainingChartData(entries: List<WeightEntry>) {
        if (entries.isEmpty()) {
            _state.update { it.copy(chartPoints = emptyList(), chartLabels = emptyList()) }
            return
        }

        // Agrupa o volume total por Ano-Mês (YYYY-MM)
        val sortedMap = entries
            .filter { it.date.length >= 7 }
            .map { entry ->
                val yearMonth = entry.date.substring(0, 7)
                yearMonth to entry.weight
            }
            .groupBy { it.first }
            .mapValues { group -> group.value.sumOf { it.second } }
            .toSortedMap()

        if (sortedMap.isEmpty()) {
            _state.update { it.copy(chartPoints = emptyList(), chartLabels = emptyList()) }
            return
        }

        val monthlyValues = sortedMap.values.toList()
        val monthKeys = sortedMap.keys.toList()

        val labels = monthKeys.map { key ->
            val monthIndex = key.substringAfter("-").toIntOrNull() ?: 1
            monthNames.getOrElse(monthIndex - 1) { "???" }
        }

        val normalizedPoints = if (monthlyValues.size == 1) {
            listOf(0.5f)
        } else {
            val maxValue = monthlyValues.maxOrNull() ?: 1.0
            val minValue = monthlyValues.minOrNull() ?: 0.0
            val range = (maxValue - minValue).coerceAtLeast(1.0)
            monthlyValues.map { vol -> ((vol - minValue) / range).toFloat() }
        }

        val first = monthlyValues.first()
        val last = monthlyValues.last()
        val change = if (first > 0) ((last - first) / first) * 100 else 0.0

        _state.update {
            it.copy(
                chartPoints = normalizedPoints,
                chartLabels = labels,
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
