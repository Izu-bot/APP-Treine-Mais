package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.remote.datasource.AuthRemoteDataSource
import com.izubot.treinemais.domain.model.LoginRequest
import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.Token
import com.izubot.treinemais.domain.model.User
import com.izubot.treinemais.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
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

    /**
     * Authenticate a user using the provided credentials and obtain authentication tokens.
     *
     * @param request The user's login credentials (e.g., email/username and password).
     * @return A Token containing access and refresh tokens and related metadata.
     */
    override suspend fun login(request: LoginRequest): Result<Token> {
        return runCatching {
            remoteDataSource.login(request)
        }
    }

    /**
     * Refreshes the authentication token using the provided refresh token.
     *
     * @param refreshToken The refresh token used to obtain a new access token.
     * @return A Result containing the refreshed `Token` on success, or a failure if the remote call fails.
     */
    override suspend fun refreshToken(refreshToken: String): Result<Token> {
        return try {
            Result.success(remoteDataSource.refreshToken(refreshToken))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Invalidates the given refresh token on the remote authentication service.
     *
     * @param refreshToken The refresh token to invalidate.
     * @return A `String` containing the server's logout confirmation message.
     */
    override suspend fun logout(refreshToken: String): Result<String> {
        return try {
            Result.success(remoteDataSource.logout(refreshToken))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}