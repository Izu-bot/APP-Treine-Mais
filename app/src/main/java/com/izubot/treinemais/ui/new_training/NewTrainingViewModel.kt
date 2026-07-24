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
                    fieldUpdate(exercise).copy(error = false, message = 0)
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

        var hasError = false
        val validatedExercises = currentState.exercises.map { exercise ->
            val isNameEmpty = exercise.name.isBlank()
            val isSetsEmpty = exercise.sets.isBlank()
            val isRepsEmpty = exercise.reps.isBlank()
            val isWeightEmpty = exercise.weight.isBlank()

            val setsValue = exercise.sets.toIntOrNull()
            val repsValue = exercise.reps.toIntOrNull()
            val weightValue = exercise.weight.toDoubleOrNull()

            val isSetsInvalid = setsValue == null || setsValue <= 0 || setsValue > 20
            val isRepsInvalid = repsValue == null || repsValue <= 0 || repsValue > 20
            val isWeightInvalid = weightValue == null || weightValue <= 0

            if (isNameEmpty || isSetsEmpty || isRepsEmpty || isWeightEmpty) {
                hasError = true
                exercise.copy(error = true, message = R.string.new_training_exercise_empty_fields)
            } else if (isSetsInvalid || isRepsInvalid || isWeightInvalid) {
                hasError = true
                exercise.copy(error = true, message = R.string.new_training_exercise_invalid_range)
            } else {
                exercise.copy(error = false, message = 0)
            }
        }

        if (hasError) {
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
                exercises = validatedExercises.map { uiExercise ->
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