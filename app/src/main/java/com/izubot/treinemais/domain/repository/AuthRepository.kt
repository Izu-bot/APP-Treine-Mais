package com.izubot.treinemais.domain.repository

import com.izubot.treinemais.domain.model.LoginRequest
import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.Token
import com.izubot.treinemais.domain.model.User

interface AuthRepository {
    suspend fun register(request: RegisterRequest): Result<User>
    suspend fun confirmEmail(token: String): Result<Unit>
    suspend fun login(request: LoginRequest): Result<Token>
}