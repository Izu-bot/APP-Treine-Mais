package com.izubot.treinemais.data.remote.dto

import com.izubot.treinemais.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val userId: String,
    val name: String,
    val email: String,
    val gender: String,
    val birthDate: String,
    val updatedAt: Long
)

fun UserDto.toDomain() = User(
    userId = userId,
    email = email,
)