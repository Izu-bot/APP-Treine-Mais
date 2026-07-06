package com.izubot.treinemais.ui.navigation

import kotlinx.serialization.Serializable

sealed interface MainRoute {

    @Serializable
    data object Home : MainRoute
    @Serializable
    data object Profile : MainRoute
    @Serializable
    data object Training : MainRoute
    @Serializable
    data object NewTraining: MainRoute
    @Serializable
    data class EditExercise(val exerciseId: String? = null, val trainingId: String): MainRoute
}