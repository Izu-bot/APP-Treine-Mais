package com.izubot.treinemais.ui.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.model.Training
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class TrainingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingUiState())
    val state = _uiState.asStateFlow()

    init {
        loadTrainings()
    }

    private fun loadTrainings() {
        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, progress = 0f) }

            for (i in 1..4) {
                delay(1000.milliseconds)
                _uiState.update { it.copy(progress = i / 4f) }
            }

            val exercise = listOf(
                Exercise(id = "1", name = "Supino Reto", sets = "4", reps = "10-12 reps")
            )

            val listTrainings = listOf<Training>(
                Training("peito", "Peito", exercise)
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    trainings = listTrainings,
                    isListEmpty = listTrainings.isEmpty()
                )
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
