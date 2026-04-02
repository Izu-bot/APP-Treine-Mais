package com.izubot.treinemais.ui.home

data class HomeUiState (
    val isNotificationActive: Boolean = false,
    val nameUser: String = "",
    val imageUri: String = "",
    val greeting: String = "",
)
