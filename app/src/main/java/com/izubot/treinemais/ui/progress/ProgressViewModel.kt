package com.izubot.treinemais.ui.progress

import android.content.Context
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.repository.ExerciseHistoryRepository
import com.izubot.treinemais.domain.repository.ExerciseRepository
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
class ProgressViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val exerciseRepository: ExerciseRepository,
    private val exerciseHistoryRepository: ExerciseHistoryRepository,
    private val uiEventManager: UiEventManager
) : ViewModel() {
    private val _state = MutableStateFlow(ProgressUiState())
    val state = _state.asStateFlow()

    private val _channel = Channel<UiEvent>()
    val channel = _channel.receiveAsFlow()

    init {
        loadAllExercises()
        observeSharedEvents()
    }

    private fun observeSharedEvents() {
        viewModelScope.launch {
            uiEventManager.events.collect { event ->
                _channel.send(event)
            }
        }
    }

    private fun loadAllExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercise()
                .onSuccess { exercises ->
                    _state.update { it.copy(allExercises = exercises) }
                }
                .onFailure {
                    uiEventManager.sendEvent(UiEvent.Error(context.getString(R.string.error_generic)))
                }
        }
    }

    fun onChangeFilter(filter: String) {
        _state.update { currentState ->
            val newFilters = if (currentState.selectedFilters.contains(filter)) {
                currentState.selectedFilters - filter
            } else {
                currentState.selectedFilters + filter
            }
            currentState.copy(selectedFilters = newFilters)
        }
    }

    fun onChangeSearchQuery(newQuery: String) {
        _state.update { currentState ->
            val filtered = if (newQuery.isBlank()) {
                emptyList()
            } else {
                currentState.allExercises.filter {
                    it.name.contains(newQuery, ignoreCase = true)
                }
            }
            currentState.copy(
                searchQuery = newQuery,
                filteredExercises = filtered,
                isExerciseSelected = false
            )
        }
    }

    fun onExerciseSelected(exercise: Exercise) {
        _state.update { it.copy(
            selectedExercise = exercise,
            searchQuery = exercise.name,
            filteredExercises = emptyList(),
            isLoading = true
        ) }
        loadExerciseHistory(exercise.id)
    }

    fun onSearchTriggered() {
        val currentQuery = _state.value.searchQuery
        val exactMatch = _state.value.allExercises.find { it.name.equals(currentQuery, ignoreCase = true) }

        if (exactMatch != null) {
            onExerciseSelected(exactMatch)
        } else {
            _state.value.filteredExercises.firstOrNull()?.let {
                onExerciseSelected(it)
            }
        }
    }

    private fun loadExerciseHistory(exerciseId: String) {
        viewModelScope.launch {
            exerciseHistoryRepository.getWeightEvolution(exerciseId).collect { entries ->
                _state.update { it.copy(
                    weightEntries = entries,
                    isLoading = false,
                    isExerciseSelected = true
                ) }
            }
        }
    }
}