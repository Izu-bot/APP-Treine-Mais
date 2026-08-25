package com.izubot.treinemais.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.domain.repository.TrainingRepository
import com.izubot.treinemais.domain.usecase.GetWeeklyProgressUseCase
import com.izubot.treinemais.utils.UiEvent
import com.izubot.treinemais.utils.UiEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeeklyProgressUseCase: GetWeeklyProgressUseCase,
    private val trainingRepository: TrainingRepository,
//    private val dataStoreRepository: PrefsRepository,
    private val uiEventManager: UiEventManager,
//    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _localState = MutableStateFlow(HomeUiState())
    val state = _localState.asStateFlow()

    private val _channel = Channel<UiEvent>()
    val channel = _channel.receiveAsFlow()

    init {
        getWeeklyProgress()
        getTrainings()
        observeSharedEvents()
    }

    private fun observeSharedEvents() {
        viewModelScope.launch {
            uiEventManager.events.collect { event ->
                _channel.send(event)
            }
        }
    }

    private fun getTrainings() {
        viewModelScope.launch {
            trainingRepository.getAllTrainings().collect { list ->
                _localState.update { it.copy(trainings = list) }
            }
        }
    }

//    private fun showFeedback(lastFeedbackTimestamp: Long?, currentDate: Long): Boolean {
//        if (lastFeedbackTimestamp == null) return false
//
//        val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000L
//        return (currentDate - lastFeedbackTimestamp) >= oneWeekInMillis
//    }
//
//    fun onFeedbackDismissed() {
//        _localState.update { it.copy(showFeedbackBottomSheet = false) }
//    }
//
//    fun onFeedbackSubmitted(
//        featureAnswer: String,
//        performanceAnswer: String,
//        navigationAnswer: String,
//        recommendAnswer: String
//    ) {
//        viewModelScope.launch {
//            // Carimbamos a data de envio para não perguntar novamente em breve
//            dataStoreRepository.saveFeedbackTimestamp(System.currentTimeMillis())
//
//            val feedback = Feedback(
//                answer = mapOf(
//                    "funcionalidade" to featureAnswer,
//                    "performance" to performanceAnswer,
//                    "navegação" to navigationAnswer,
//                    "recomendar" to recommendAnswer
//                ),
//                date = LocalDate.now().toString()
//            )
//
//            firebaseRepository.submitFeedback(feedback)
//                .onSuccess {
//                    uiEventManager.sendEvent(UiEvent.Success("Feedback enviado com sucesso! Obrigado."))
//                }
//                .onFailure {
//                    uiEventManager.sendEvent(UiEvent.Error("Erro ao enviar feedback."))
//                }
//
//            onFeedbackDismissed()
//        }
//    }

    private fun getWeeklyProgress() {
        viewModelScope.launch {
            getWeeklyProgressUseCase().collect { list ->
                _localState.update { it.copy(weeklyProgress = list) }
            }
        }
    }
}
