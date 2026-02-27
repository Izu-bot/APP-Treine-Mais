package com.izubot.treinemais.data.remote.dto

import com.izubot.treinemais.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val password: String
)

fun UserDto.toDomain() = User(
    id = id,
    email = email,
    password = password
)