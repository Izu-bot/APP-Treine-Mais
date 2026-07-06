package com.izubot.treinemais.ui.training

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.domain.repository.TrainingRepository
import com.izubot.treinemais.utils.UiEvent
import com.izubot.treinemais.utils.UiEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val trainingRepository: TrainingRepository,
    private val uiEventManager: UiEventManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingUiState())
    val state = _uiState.asStateFlow()

    private val _channel = Channel<UiEvent>()
    val channel = _channel.receiveAsFlow()

    init {
        observeTrainings()
        observeSharedEvents()
    }

    private fun observeSharedEvents() {
        viewModelScope.launch {
            uiEventManager.events.collectLatest { event ->
                _channel.send(event)
            }
        }
    }

    private fun observeTrainings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            trainingRepository.getAllTrainings().collect { listTrainings ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        trainings = listTrainings,
                        isListEmpty = listTrainings.isEmpty()
                    )
                }
            }
        }
    }

    fun onToggleCard(trainingId: String) {
        _uiState.update { currentState ->
            val newIds = if (currentState.expandedCardIds.contains(trainingId)) {
                currentState.expandedCardIds - trainingId
            } else {
                currentState.expandedCardIds + trainingId
            }
            currentState.copy(expandedCardIds = newIds)
        }
    }

    fun deleteTraining(training: Training) {
        viewModelScope.launch {
            trainingRepository.deleteTraining(training)
                .onSuccess {
                    uiEventManager.sendEvent(UiEvent.Success(context.getString(R.string.training_delete_success)))
                }
                .onFailure {
                    _channel.send(UiEvent.Error(context.getString(R.string.training_delete_error)))
                }
        }
    }
}
