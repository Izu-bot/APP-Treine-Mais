package com.izubot.treinemais.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AuthRoute {
    @Serializable
    data object Splash : AuthRoute
    @Serializable
    data object Welcome: AuthRoute
    @Serializable
    data class Login(val token: String? = null) : AuthRoute
    @Serializable
    data object Register : AuthRoute
    @Serializable
    data object Confirm : AuthRoute
    @Serializable
    data object Home : AuthRoute
}