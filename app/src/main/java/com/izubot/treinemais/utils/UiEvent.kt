package com.izubot.treinemais.utils

sealed class UiEvent {
    data class Toast(val message: String): UiEvent()
}