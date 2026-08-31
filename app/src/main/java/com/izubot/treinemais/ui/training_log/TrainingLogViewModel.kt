package com.izubot.treinemais.ui.training_log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.izubot.treinemais.domain.model.ExerciseHistory
import com.izubot.treinemais.domain.repository.ExerciseHistoryRepository
import com.izubot.treinemais.domain.repository.TrainingHistoryRepository
import com.izubot.treinemais.domain.repository.TrainingRepository
import com.izubot.treinemais.ui.navigation.MainRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class TrainingLogViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val trainingHistoryRepository: TrainingHistoryRepository,
    private val exerciseHistoryRepository: ExerciseHistoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _trainingLogRoute = savedStateHandle.toRoute<MainRoute.TrainingLog>()
    private val _uiState = MutableStateFlow(TrainingLogUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadExercise()
    }

    private fun loadExercise() {
        viewModelScope.launch {
            val training = trainingRepository.getTraining(_trainingLogRoute.trainingId).first()

            val initialSets = training!!.exercises.associate { exercise ->
                val numberOfSets = exercise.sets?.toIntOrNull() ?: 0

                exercise.id to List(numberOfSets) {
                    ExerciseSetLog(
                        reps = exercise.reps ?: "",
                        weight = exercise.weight ?: ""
                    )
                }
            }

            _uiState.update { it.copy(training = training, exerciseSet = initialSets) }
        }
    }

    fun addSet(exerciseId: String) {
        _uiState.update { currentState ->
            val currentSets = currentState.exerciseSet[exerciseId] ?: emptyList()
            val updateSets = currentSets + ExerciseSetLog()
            val updateMap = currentState.exerciseSet + (exerciseId to updateSets)

            currentState.copy(
                exerciseSet = updateMap,
                confirmedExerciseIds = calculateCompletedExercises(updateMap)
            )
        }
    }

    fun removeSet(exerciseId: String) {
        _uiState.update { currentState ->
            val currentSets = currentState.exerciseSet[exerciseId] ?: return@update currentState
            val updateSet = currentSets.dropLast(1)
            val updateMap = currentState.exerciseSet + (exerciseId to updateSet)

            currentState.copy(
                exerciseSet = updateMap,
                confirmedExerciseIds = calculateCompletedExercises(updateMap)
            )
        }
    }

    fun handlerDialog() {
        _uiState.update { it.copy(openDialog = !it.openDialog) }
    }

    fun finishTraining(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val date = LocalDate.now()
            val trainingId = _trainingLogRoute.trainingId
            val training = _uiState.value.training ?: return@launch

            val historyId = trainingHistoryRepository.markDayAsCompleted(date.toString(), trainingId)

            _uiState.value.exerciseSet.forEach { (exerciseId, sets) ->
                val exercise = training.exercises.find { it.id == exerciseId }
                val completedSets = sets.filter { it.isCompleted }

                if (exercise != null && completedSets.isNotEmpty()) {
                    val maxWeight = completedSets.maxOfOrNull { it.weight.toDoubleOrNull() ?: 0.0 } ?: 0.0
                    val totalReps = completedSets.sumOf { it.reps.toIntOrNull() ?: 0 }

                    exerciseHistoryRepository.insertExerciseTraining(
                        ExerciseHistory(
                            trainingHistoryId = historyId,
                            exerciseId = exerciseId,
                            exerciseName = exercise.name,
                            weight = maxWeight,
                            reps = totalReps,
                            sets = completedSets.size,
                            date = date
                        )
                    )
                }
            }

            // Simula um tempo para a animação de "salvando"
            delay(1000)
            
            _uiState.update { it.copy(isSaving = false, showTrophy = true) }
            
            // Tempo para o usuário ver o troféu
            delay(2000)
            
            _uiState.update { it.copy(isTrainingCompleted = true) }
            onComplete()
        }
    }

    fun updateSetLog(
        exerciseId: String,
        setIndex: Int,
        reps: String? = null,
        weight: String? = null
    ) {
        _uiState.update { currentState ->
            val currentSets = currentState.exerciseSet[exerciseId]?.toMutableList()
                ?: return@update currentState

            val setLog = currentSets[setIndex]

            currentSets[setIndex] = setLog.copy(
                reps = reps ?: setLog.reps,
                weight = weight ?: setLog.weight
            )

            val updatedMap = currentState.exerciseSet + (exerciseId to currentSets)
            currentState.copy(
                exerciseSet = updatedMap,
                confirmedExerciseIds = calculateCompletedExercises(updatedMap)
            )
        }
    }

    fun toggleExerciseConfirmation(exerciseId: String, setIndex: Int) {
        _uiState.update { currentState ->
            val currentSets = currentState.exerciseSet[exerciseId]?.toMutableList()
                ?: return@update currentState

            val setLog = currentSets[setIndex]
            currentSets[setIndex] = setLog.copy(isCompleted = !setLog.isCompleted)

            val updatedMap = currentState.exerciseSet + (exerciseId to currentSets)
            currentState.copy(
                exerciseSet = updatedMap,
                confirmedExerciseIds = calculateCompletedExercises(updatedMap)
            )
        }
    }
    
    private fun calculateCompletedExercises(exerciseMap: Map<String, List<ExerciseSetLog>>):
            Set<String> {
        return exerciseMap.filter { (_, sets) ->
            sets.isNotEmpty() && sets.all { it.isCompleted }
        }.keys
    }
}