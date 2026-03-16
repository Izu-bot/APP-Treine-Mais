package com.izubot.treinemais.data.remote.dto

import com.izubot.treinemais.domain.model.Token
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class TokenDto(
    @SerialName("AccessToken")
    val accessToken: String,
    @SerialName("RefreshToken")
    val refreshToken: String
)
fun TokenDto.toDomain() = Token(
    accessToken = accessToken,
    refreshToken = refreshToken
)