package com.izubot.treinemais.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppDestinations {
    @Serializable
    data object Home : AppDestinations
}