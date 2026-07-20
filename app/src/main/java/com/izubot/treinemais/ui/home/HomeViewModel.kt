package com.izubot.treinemais.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.domain.repository.TrainingHistoryRepository
import com.izubot.treinemais.domain.repository.TrainingRepository
import com.izubot.treinemais.domain.usecase.GetWeeklyProgressUseCase
import com.izubot.treinemais.utils.FocusManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeeklyProgressUseCase: GetWeeklyProgressUseCase,
    private val trainingRepository: TrainingRepository,
    private val historyRepository: TrainingHistoryRepository,
    private val focusManager: FocusManager
) : ViewModel() {

    private val _localState = MutableStateFlow(HomeUiState())
    val state = _localState.asStateFlow()

    val focusActions = focusManager.focusActions

    init {
        getWeeklyProgress()
        getTrainings()
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
            focusManager.clearFocus()
            
            // Persist completion to database
            historyRepository.markDayAsCompleted(LocalDate.now().toString())

            _localState.update { currentState ->
                currentState.copy(
                    selectedTraining = null,
                    isTrainingCompleted = true
                )
            }
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
