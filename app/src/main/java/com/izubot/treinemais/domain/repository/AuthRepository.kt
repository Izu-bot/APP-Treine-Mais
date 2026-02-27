package com.izubot.treinemais.domain.repository

import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.User

interface AuthRepository {
    suspend fun register(request: RegisterRequest): Result<User>
    suspend fun confirmEmail(token: String): Result<Unit>
}