package com.izubot.treinemais.data.remote.dto

import com.izubot.treinemais.domain.model.LoginRequest
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

fun LoginRequest.toDto() = LoginRequestDto(
    email = email,
    password = password
)