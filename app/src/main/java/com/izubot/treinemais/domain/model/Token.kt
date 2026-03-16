package com.izubot.treinemais.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(
    @SerialName("AccessToken")
    val accessToken: String,
    @SerialName("RefreshToken")
    val refreshToken: String
)