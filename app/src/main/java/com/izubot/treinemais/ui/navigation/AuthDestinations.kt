package com.izubot.treinemais.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AuthRoute {
    @Serializable
    data object Splash : AuthRoute
    @Serializable
    data object Welcome: AuthRoute
    @Serializable
    data object Login : AuthRoute
    @Serializable
    data object Register : AuthRoute
}