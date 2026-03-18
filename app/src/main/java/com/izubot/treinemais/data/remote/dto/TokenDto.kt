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

/**
 * Converts this DTO into a domain Token.
 *
 * @return A Token containing the same accessToken and refreshToken values as this DTO.
 */
fun TokenDto.toDomain() = Token(
    accessToken = accessToken,
    refreshToken = refreshToken
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("RefreshToken")
    val refreshToken: String
)