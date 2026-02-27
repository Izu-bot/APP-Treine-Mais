package com.izubot.treinemais.data.remote.api

import com.izubot.treinemais.data.remote.dto.RegisterRequestDto
import com.izubot.treinemais.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("rota")
    suspend fun register(@Body body: RegisterRequestDto) : UserDto
}
