package com.izubot.treinemais.ui.new_training

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NewTrainingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(NewTrainingUiState())
    val state: StateFlow<NewTrainingUiState> = _uiState.asStateFlow()

    fun onTrainingNameChange(newName: String) {
        _uiState.update { it.copy(trainingName = newName) }
    }

    fun addExercise() {
        _uiState.update { it.copy(exercises = it.exercises + ExerciseUiState())}
    }

    fun undoRemove(index: Int, exercise: ExerciseUiState) {
        _uiState.update { currentState ->
            val newList = currentState.exercises.toMutableList()
            newList.add(index, exercise)
            currentState.copy(exercises = newList)
        }
    }

    fun removeExercise(exercise: ExerciseUiState) {
        _uiState.update { currentState ->
            currentState.copy( exercises = currentState.exercises.filter {
                it.id != exercise.id
            })
        }
    }

    fun updateExercise(exerciseId: String, fieldUpdate: (ExerciseUiState) -> ExerciseUiState) {
        _uiState.update { currentState ->
            val updateList = currentState.exercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    fieldUpdate(exercise)
                } else {
                    exercise
                }
            }

            currentState.copy(exercises = updateList)
        }
    }
}