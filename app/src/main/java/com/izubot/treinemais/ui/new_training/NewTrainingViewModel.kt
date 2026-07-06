package com.izubot.treinemais.ui.new_training

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.domain.repository.TrainingRepository
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
    private val uiEventManager: UiEventManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewTrainingUiState())
    val state: StateFlow<NewTrainingUiState> = _uiState.asStateFlow()
    private val _channel = Channel<UiEvent>()
    val channel = _channel.receiveAsFlow()

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

    fun saveTraining(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val trainingName = currentState.trainingName.trim()

        if (trainingName.isBlank()) {
            viewModelScope.launch {
                _channel.send(UiEvent.Error(context.getString(R.string.new_training_name_empty)))
            }
            return
        }

        if (currentState.exercises.isEmpty()) {
            viewModelScope.launch {
                _channel.send(UiEvent.Error(context.getString(R.string.new_training_no_exercises)))
            }
            return
        }

        val hasEmptyExercise = currentState.exercises.any {
            it.name.isBlank() || it.sets.isBlank() || it.reps.isBlank() || it.weight.isBlank()
        }

        if (hasEmptyExercise) {
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