package com.izubot.treinemais.data.remote.datasource

import com.izubot.treinemais.data.remote.api.AuthApi
import com.izubot.treinemais.data.remote.dto.toDomain
import com.izubot.treinemais.data.remote.dto.toDto
import com.izubot.treinemais.domain.model.LoginRequest
import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.Token
import com.izubot.treinemais.domain.model.User
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthApi
) : AuthRemoteDataSource {
    override suspend fun register(request: RegisterRequest) : User {
        return api.register(request.toDto()).toDomain()
    }

    override suspend fun confirmEmail(token: String) {
        api.confirmEmail(token)
    }

    override suspend fun login(request: LoginRequest) : Token {
        return api.login(request.toDto()).toDomain()
    }
}