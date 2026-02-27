package com.izubot.treinemais.data.remote.dto

import com.izubot.treinemais.domain.model.RegisterRequest
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val profile: ProfileDto,
)

fun RegisterRequest.toDto() = RegisterRequestDto(
    email = email,
    password = password,
    profile = ProfileDto(
        name = name,
        gender = gender,
        goals = goals
    )
)