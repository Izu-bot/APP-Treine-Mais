package com.izubot.treinemais.data.remote.dto

import com.izubot.treinemais.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val userId: String,
    val email: String,
)

fun UserDto.toDomain() = User(
    userId = userId,
    email = email,
)