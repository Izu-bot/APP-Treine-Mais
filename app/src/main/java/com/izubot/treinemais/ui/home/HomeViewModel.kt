package com.izubot.treinemais.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.domain.model.ExerciseHistory
import com.izubot.treinemais.domain.model.Feedback
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.domain.repository.ExerciseHistoryRepository
import com.izubot.treinemais.domain.repository.FirebaseRepository
import com.izubot.treinemais.domain.repository.PrefsRepository
import com.izubot.treinemais.domain.repository.TrainingHistoryRepository
import com.izubot.treinemais.domain.repository.TrainingRepository
import com.izubot.treinemais.domain.usecase.GetWeeklyProgressUseCase
import com.izubot.treinemais.utils.FocusManager
import com.izubot.treinemais.utils.UiEvent
import com.izubot.treinemais.utils.UiEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeeklyProgressUseCase: GetWeeklyProgressUseCase,
    private val trainingRepository: TrainingRepository,
    private val historyRepository: TrainingHistoryRepository,
    private val exerciseHistoryRepository: ExerciseHistoryRepository,
    private val focusManager: FocusManager,
    private val dataStoreRepository: PrefsRepository,
    private val uiEventManager: UiEventManager,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _localState = MutableStateFlow(HomeUiState())
    val state = _localState.asStateFlow()

    private val _channel = Channel<UiEvent>()
    val channel = _channel.receiveAsFlow()

    val focusActions = focusManager.focusActions

    init {
        getWeeklyProgress()
        getTrainings()
        observeSharedEvents()
    }

    private fun observeSharedEvents() {
        viewModelScope.launch {
            uiEventManager.events.collect { event ->
                _channel.send(event)
            }
        }
    }

    private fun getTrainings() {
        viewModelScope.launch {
            trainingRepository.getAllTrainings().collect { list ->
                _localState.update { it.copy(trainings = list) }
            }
        }
    }

    fun selectTraining(training: Training) {
        viewModelScope.launch {
            focusManager.clearFocus()
            val initialWeights = training.exercises.associate { exercise ->
                val numSets = exercise.sets?.toIntOrNull() ?: 1
                exercise.id to List(numSets) { "" }
            }

            _localState.update { 
                it.copy(
                    selectedTraining = training,
                    exerciseWeights = initialWeights,
                    isTrainingCompleted = false,
                    confirmedExerciseIds = emptySet()
                ) 
            }
        }
    }

    fun confirmExercise(exerciseId: String) {
        _localState.update { currentState ->
            val weights = currentState.exerciseWeights[exerciseId]
            if (weights != null && weights.all { it.isNotBlank() }) {
                currentState.copy(
                    confirmedExerciseIds = currentState.confirmedExerciseIds + exerciseId
                )
            } else {
                currentState
            }
        }
    }

    fun unlockExercise(exerciseId: String) {
        _localState.update { currentState ->
            currentState.copy(
                confirmedExerciseIds = currentState.confirmedExerciseIds - exerciseId
            )
        }
    }

    fun onWeightChange(exerciseId: String, setIndex: Int, weight: String) {
        val currentState = _localState.value
        if (currentState.confirmedExerciseIds.contains(exerciseId)) return

        val currentWeightsMap = currentState.exerciseWeights
        val exerciseWeights = currentWeightsMap[exerciseId] ?: return
        
        if (setIndex in exerciseWeights.indices) {
            val updatedWeights = exerciseWeights.toMutableList().apply {
                this[setIndex] = weight
            }
            _localState.update { state ->
                state.copy(
                    exerciseWeights = state.exerciseWeights + (exerciseId to updatedWeights)
                )
            }
        }
    }

    fun completeTraining() {
        viewModelScope.launch {
            val currentState  = _localState.value
            val training = currentState.selectedTraining ?: return@launch

            focusManager.clearFocus()

            val trainingHistoryId = historyRepository.markDayAsCompleted(
                date = LocalDate.now().toString(),
                trainingId = training.id
            )

            training.exercises.forEach { exercise ->
                val weights = currentState.exerciseWeights[exercise.id] ?: return@forEach

                val repsInt = exercise.reps?.filter { it.isDigit() }?.toIntOrNull() ?: 0

                weights.forEach { weightStr ->
                    val weightDouble = weightStr.toDoubleOrNull() ?: 0.0
                    if (weightDouble > 0) {
                        val history = ExerciseHistory(
                            trainingHistoryId = trainingHistoryId,
                            exerciseId = exercise.id,
                            exerciseName = exercise.name,
                            weight = weightDouble,
                            reps = repsInt,
                            sets = exercise.sets?.toIntOrNull() ?: 1,
                            date = LocalDate.now()
                        )
                        exerciseHistoryRepository.insertExerciseTraining(history)
                    }
                }
            }

            val lastSurvey = dataStoreRepository.getLastFeedbackTimestamp()
            val currentDate = System.currentTimeMillis()

            if (showFeedback(lastSurvey, currentDate)) {
                _localState.update { it.copy(showFeedbackBottomSheet = true) }
            } else if (lastSurvey == null) {
                dataStoreRepository.saveFeedbackTimestamp(currentDate)
            }

            _localState.update { state ->
                state.copy(
                    selectedTraining = null,
                    isTrainingCompleted = true
                )
            }
        }
    }

    private fun showFeedback(lastFeedbackTimestamp: Long?, currentDate: Long): Boolean {
        if (lastFeedbackTimestamp == null) return false

        val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000L
        return (currentDate - lastFeedbackTimestamp) >= oneWeekInMillis
    }

    fun onFeedbackDismissed() {
        _localState.update { it.copy(showFeedbackBottomSheet = false) }
    }

    fun onFeedbackSubmitted(
        featureAnswer: String,
        performanceAnswer: String,
        navigationAnswer: String,
        recommendAnswer: String
    ) {
        viewModelScope.launch {
            // Carimbamos a data de envio para não perguntar novamente em breve
            dataStoreRepository.saveFeedbackTimestamp(System.currentTimeMillis())

            val feedback = Feedback(
                answer = mapOf(
                    "funcionalidade" to featureAnswer,
                    "performance" to performanceAnswer,
                    "navegação" to navigationAnswer,
                    "recomendar" to recommendAnswer
                ),
                date = LocalDate.now().toString()
            )

            firebaseRepository.submitFeedback(feedback)
                .onSuccess {
                    uiEventManager.sendEvent(UiEvent.Success("Feedback enviado com sucesso! Obrigado."))
                }
                .onFailure {
                    uiEventManager.sendEvent(UiEvent.Error("Erro ao enviar feedback."))
                }

            onFeedbackDismissed()
        }
    }

    fun resetTrainingSelection() {
        focusManager.clearFocus()
        _localState.update { it.copy(selectedTraining = null) }
    }

    private fun getWeeklyProgress() {
        viewModelScope.launch {
            getWeeklyProgressUseCase().collect { list ->
                _localState.update { it.copy(weeklyProgress = list) }
            }
        }
    }
}
