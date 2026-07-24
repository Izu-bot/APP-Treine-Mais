package com.izubot.treinemais.ui.new_training

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.domain.repository.TrainingRepository
import com.izubot.treinemais.utils.FocusManager
import com.izubot.treinemais.utils.UiEvent
import com.izubot.treinemais.utils.UiEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewTrainingViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val trainingRepository: TrainingRepository,
    private val uiEventManager: UiEventManager,
    private val focusManager: FocusManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewTrainingUiState())
    val state: StateFlow<NewTrainingUiState> = _uiState.asStateFlow()
    private val _channel = Channel<UiEvent>()
    val channel = _channel.receiveAsFlow()

    fun onTrainingNameChange(newName: String) {
        _uiState.update { it.copy(trainingName = newName, error = false, trainingNameError = false, message = 0) }
    }

    fun addExercise() {
        _uiState.update { it.copy(exercises = it.exercises + ExerciseUiState(), error = false) }
    }

    fun undoRemove(index: Int, exercise: ExerciseUiState) {
        _uiState.update { currentState ->
            val newList = currentState.exercises.toMutableList()
            newList.add(index, exercise)
            currentState.copy(exercises = newList, error = false)
        }
    }

    fun removeExercise(exercise: ExerciseUiState) {
        _uiState.update { currentState ->
            currentState.copy( exercises = currentState.exercises.filter {
                it.id != exercise.id
            }, error = false)
        }
    }

    fun updateExercise(exerciseId: String, fieldUpdate: (ExerciseUiState) -> ExerciseUiState) {
        _uiState.update { currentState ->
            val updateList = currentState.exercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    val updated = fieldUpdate(exercise)

                    val sets = updated.sets.toIntOrNull()
                    val reps = updated.reps.toIntOrNull()
                    val weight = updated.weight.replace(",", ".").toDoubleOrNull()

                    val isSetsInvalid = updated.sets.isNotBlank() && (sets == null || sets <= 0 || sets > 20)
                    val isRepsInvalid = updated.reps.isNotBlank() && (reps == null || reps <= 0 || reps > 20)
                    val isWeightInvalid = updated.weight.isNotBlank() && (weight == null || weight <= 0)

                    val hasRangeError = isSetsInvalid || isRepsInvalid || isWeightInvalid

                    updated.copy(
                        error = hasRangeError,
                        message = if (hasRangeError) R.string.new_training_exercise_invalid_range else 0
                    )
                } else {
                    exercise
                }
            }
            currentState.copy(exercises = updateList, error = false, trainingNameError = false, message = 0)
        }
    }

    fun saveTraining(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val trainingName = currentState.trainingName.trim()

        if (trainingName.isBlank()) {
            viewModelScope.launch {
                _channel.send(UiEvent.Error(context.getString(R.string.new_training_name_empty)))
            }

            _uiState.update { it.copy(
                error = true,
                trainingNameError = true,
                message = R.string.new_training_name_empty)
            }

            return
        }

        if (currentState.exercises.isEmpty()) {
            viewModelScope.launch {
                _channel.send(UiEvent.Error(context.getString(R.string.new_training_no_exercises)))
            }

            _uiState.update { it.copy(error = true, message = R.string.new_training_no_exercises) }
            return
        }

        var hasExerciseError = false
        val validatedExercises = currentState.exercises.map { exercise ->
            val isEmpty = exercise.name.isBlank() || exercise.sets.isBlank() ||
                    exercise.reps.isBlank() || exercise.weight.isBlank()

            if (isEmpty) {
                hasExerciseError = true
                exercise.copy(error = true, message = R.string.new_training_exercise_empty_fields)
            } else if (exercise.error) {
                hasExerciseError = true
                exercise
            } else {
                exercise
            }
        }

        if (hasExerciseError) {
            _uiState.update { it.copy(exercises = validatedExercises, error = true) }

            viewModelScope.launch {
                _channel.send(UiEvent.Error(context.getString(R.string.new_training_exercise_empty_fields)))
            }
            return
        }


        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val domainTraining = Training(
                id = UUID.randomUUID().toString(),
                title = trainingName,
                exercises = currentState.exercises.map { uiExercise ->
                    Exercise(
                        id = uiExercise.id,
                        name = uiExercise.name,
                        sets = uiExercise.sets,
                        reps = uiExercise.reps,
                        weight = uiExercise.weight,
                        description = ""
                    )
                }
            )

            trainingRepository.insertTraining(domainTraining)
                .onSuccess {
                    focusManager.clearFocus()
                    uiEventManager.sendEvent(UiEvent.Success(context.getString(R.string.new_training_save_success)))
                    onSuccess()
                }
                .onFailure {
                    _channel.send(UiEvent.Error(context.getString(R.string.new_training_save_error)))
                }

            _uiState.update { it.copy(isSaving = false) }
        }
    }
}