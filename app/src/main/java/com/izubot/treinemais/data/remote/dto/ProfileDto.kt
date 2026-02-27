package com.izubot.treinemais.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val name: String,
    val gender: String? = null,
    val birthDate: String? = null,
    val height: Float? = null,
    val weight: Float? = null,
    val goals: String? = null
)