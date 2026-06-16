package com.izubot.treinemais.ui.training

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingUiState())
    val state = _uiState.asStateFlow()

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
