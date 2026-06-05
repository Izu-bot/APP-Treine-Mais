package com.izubot.treinemais.data.remote.dto

import com.izubot.treinemais.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserApiDto(
    val userId: String,
    val email: String,
    val name: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

fun UserApiDto.toDomain() = User(
    userId = userId,
    email = email,
)