package com.izubot.treinemais.data.remote.datasource

import com.izubot.treinemais.data.remote.api.AuthApi
import com.izubot.treinemais.data.remote.dto.RefreshTokenRequest
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

    /**
     * Authenticate a user using the provided credentials and obtain an authentication token.
     *
     * @param request The user's login credentials.
     * @return An authentication `Token` for the authenticated user.
     */
    override suspend fun login(request: LoginRequest) : Token {
        return api.login(request.toDto()).toDomain()
    }

    /**
     * Refreshes the authentication credentials using the provided refresh token.
     *
     * @param refreshToken The refresh token string used to request new credentials.
     * @return A [Token] containing the refreshed access (and refresh) token information.
     */
    override suspend fun refreshToken(refreshToken: String): Token {
        return api.refresh(RefreshTokenRequest(refreshToken)).toDomain()
    }

    /**
     * Logs out the user by sending the provided refresh token to the remote API to invalidate it.
     *
     * @param refreshToken The refresh token to be invalidated.
     * @return The API response message as a `String`.
     */
    override suspend fun logout(refreshToken: String): String {
        return api.logout(RefreshTokenRequest(refreshToken))
    }
}