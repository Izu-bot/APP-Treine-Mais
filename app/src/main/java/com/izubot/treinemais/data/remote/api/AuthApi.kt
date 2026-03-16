package com.izubot.treinemais.data.remote.api

import com.izubot.treinemais.data.remote.dto.LoginRequestDto
import com.izubot.treinemais.data.remote.dto.RegisterRequestDto
import com.izubot.treinemais.data.remote.dto.TokenDto
import com.izubot.treinemais.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/auth/register")
    suspend fun register(@Body body: RegisterRequestDto) : UserDto

    @GET("/auth/confirm-email")
    suspend fun confirmEmail(@Query("token") token: String)
    @POST("/auth/login")
    suspend fun login(@Body body: LoginRequestDto) : TokenDto
}