package com.izubot.treinemais.ui.training_log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.izubot.treinemais.ui.navigation.MainRoute
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TrainingLogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _trainingLogRoute = savedStateHandle.toRoute<MainRoute.TrainingLog>()
    private val _uiState = MutableStateFlow(TrainingLogUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(teste = _trainingLogRoute.trainingId)
        }
    }
}