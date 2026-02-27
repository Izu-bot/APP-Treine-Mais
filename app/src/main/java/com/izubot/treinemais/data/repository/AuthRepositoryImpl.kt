package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.remote.datasource.AuthRemoteDataSource
import com.izubot.treinemais.domain.model.RegisterRequest
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
}