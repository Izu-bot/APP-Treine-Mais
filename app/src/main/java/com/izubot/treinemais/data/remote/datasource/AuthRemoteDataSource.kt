package com.izubot.treinemais.data.remote.datasource

import com.izubot.treinemais.domain.model.LoginRequest
import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.Token
import com.izubot.treinemais.domain.model.User

interface AuthRemoteDataSource {
    suspend fun register(request: RegisterRequest): User
    suspend fun confirmEmail(token: String)
    suspend fun login(request: LoginRequest): Token
    suspend fun refreshToken(refreshToken: String): Token
    suspend fun logout(refreshToken: String): String
}