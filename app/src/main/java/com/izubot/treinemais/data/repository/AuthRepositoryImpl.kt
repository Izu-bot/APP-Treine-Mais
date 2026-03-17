package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.remote.datasource.AuthRemoteDataSource
import com.izubot.treinemais.domain.model.LoginRequest
import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.Token
import com.izubot.treinemais.domain.model.User
import com.izubot.treinemais.domain.repository.AuthRepository
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun register(request: RegisterRequest): Result<User> {
        return runCatching {
            remoteDataSource.register(request)
        }
    }

    override suspend fun confirmEmail(token: String): Result<Unit> {
        return runCatching {
            remoteDataSource.confirmEmail(token)
        }
    }

    override suspend fun login(request: LoginRequest): Result<Token> {
        return runCatching {
            remoteDataSource.login(request)
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<Token> {
        return runCatching {
            remoteDataSource.refreshToken(refreshToken)
        }
    }

    override suspend fun logout(refreshToken: String): Result<String> {
        return runCatching {
            remoteDataSource.logout(refreshToken)
        }
    }

}