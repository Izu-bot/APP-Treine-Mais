package com.izubot.treinemais.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SyncPayloadDto(
    val user: UserDto
    // Mais campos relevantes para sincronização, se houver
)

@Serializable
data class SyncResponseDto(
    val success: Boolean
)