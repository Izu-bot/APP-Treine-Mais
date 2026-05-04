package com.izubot.treinemais.data.remote.api

import com.izubot.treinemais.data.remote.dto.SyncPayloadDto
import com.izubot.treinemais.data.remote.dto.SyncResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface SyncApi {
    @POST("sync")
    suspend fun sync(@Body syncPayload: SyncPayloadDto): SyncResponseDto
}