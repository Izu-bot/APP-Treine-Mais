package com.izubot.treinemais.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.domain.usecase.GetNotificationUseCase
import com.izubot.treinemais.domain.usecase.GetProfileImageUriUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileImageUriUseCase: GetProfileImageUriUseCase,
    private val getNotificationUseCase: GetNotificationUseCase
) : ViewModel() {

    private val _localState = MutableStateFlow(HomeUiState())
    
    val state: StateFlow<HomeUiState> = combine(
        _localState,
        getProfileImageUriUseCase.imageUrlCache,
        getNotificationUseCase.notificationCache
    ) { local, imageUri, notificationEnabled ->
        local.copy(
            imageUri = imageUri,
            isNotificationActive = notificationEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = _localState.value
    )

    fun greet(): String {
        val currentHour = LocalTime.now()

        return when (currentHour.hour) {
            in 6..11 -> "Bom dia,"
            in 12..17 -> "Boa tarde,"
            else -> "Boa noite,"
        }
    }
}
