package com.izubot.treinemais.ui.navigation

import kotlinx.serialization.Serializable

sealed interface MainRoute {

    @Serializable
    data object Home : MainRoute
}