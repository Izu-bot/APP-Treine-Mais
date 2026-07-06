package com.izubot.treinemais.ui.edit_exercise

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.repository.ExerciseRepository
import com.izubot.treinemais.ui.navigation.MainRoute
import com.izubot.treinemais.utils.FocusManager
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
class EditExerciseViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val exerciseRepository: ExerciseRepository,
    private val uiEventManager: UiEventManager,
    private val focusManager: FocusManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val editExerciseRoute = savedStateHandle.toRoute<MainRoute.EditExercise>()

    private val _uiState = MutableStateFlow(EditExerciseUiState())
    val state = _uiState.asStateFlow()

    private val _channel = Channel<UiEvent>()
    val channel = _channel.receiveAsFlow()

    init {
        if (editExerciseRoute.exerciseId != null) {
            loadExercise()
        }
    }

    private fun loadExercise() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            exerciseRepository.getExercise(editExerciseRoute.exerciseId!!)
                .onSuccess { exercise ->
                    if (exercise != null) {
                        _uiState.update {
                            it.copy(
                                id = exercise.id,
                                name = exercise.name,
                                sets = exercise.sets ?: "",
                                reps = exercise.reps ?: "",
                                weight = exercise.weight ?: "",
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Exercício não encontrado") }
                    }
                }
                .onFailure { t ->
                    _uiState.update { it.copy(isLoading = false, error = t.message) }
                }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onSetsChange(sets: String) {
        _uiState.update { it.copy(sets = sets) }
    }

    fun onRepsChange(reps: String) {
        _uiState.update { it.copy(reps = reps) }
    }

    fun onWeightChange(weight: String) {
        _uiState.update { it.copy(weight = weight) }
    }

    fun updateExercise(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        
        if (currentState.name.isBlank() || currentState.sets.isBlank() || 
            currentState.reps.isBlank() || currentState.weight.isBlank()) {
            viewModelScope.launch {
                _channel.send(UiEvent.Error(context.getString(R.string.new_training_exercise_empty_fields)))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val exercise = Exercise(
                id = currentState.id.ifBlank { java.util.UUID.randomUUID().toString() },
                name = currentState.name,
                sets = currentState.sets,
                reps = currentState.reps,
                weight = currentState.weight
            )

            val result = if (currentState.id.isBlank()) {
                // New exercise
                (exerciseRepository as com.izubot.treinemais.data.repository.ExerciseRepositoryImpl)
                    .insertExerciseWithTrainingId(exercise, editExerciseRoute.trainingId)
            } else {
                // Update existing
                exerciseRepository.updateExercise(exercise)
            }

            result.onSuccess {
                val msg = if (currentState.id.isBlank()) "Exercício adicionado com sucesso" else "Exercício atualizado com sucesso"
                focusManager.clearFocus()
                uiEventManager.sendEvent(UiEvent.Success(msg))
                onSuccess()
            }
            .onFailure {
                val msg = if (currentState.id.isBlank()) "Erro ao adicionar exercício" else "Erro ao atualizar exercício"
                _channel.send(UiEvent.Error(msg))
            }
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    fun deleteExercise(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val exercise = Exercise(
                id = _uiState.value.id,
                name = _uiState.value.name
            )
            exerciseRepository.deleteExercise(exercise)
                .onSuccess {
                    focusManager.clearFocus()
                    uiEventManager.sendEvent(UiEvent.Success("Exercício excluído com sucesso"))
                    onSuccess()
                }
                .onFailure {
                    _channel.send(UiEvent.Error("Erro ao excluir exercício"))
                }
        }
    }
}