package com.izubot.treinemais.data.remote.datasource

import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.User

interface AuthRemoteDataSource {
    suspend fun register(request: RegisterRequest): User
    suspend fun confirmEmail(token: String)
}