package com.izubot.treinemais.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _localState = MutableStateFlow(HomeUiState())
    val state = _localState

    // Carregar dados (imagem do usuário) do Room se existir

    fun greet(): String {
        val currentHour = LocalTime.now()

        return when (currentHour.hour) {
            in 6..11 -> "Bom dia,"
            in 12..17 -> "Boa tarde,"
            else -> "Boa noite,"
        }
    }
}
