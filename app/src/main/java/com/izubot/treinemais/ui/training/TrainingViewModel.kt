package com.izubot.treinemais.ui.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.domain.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingUiState())
    val state = _uiState.asStateFlow()

    init {
        observeTrainings()
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
}
