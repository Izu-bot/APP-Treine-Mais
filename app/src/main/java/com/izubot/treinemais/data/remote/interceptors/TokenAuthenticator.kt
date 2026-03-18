package com.izubot.treinemais.data.remote.interceptors

import android.util.Log
import com.izubot.treinemais.data.local.SessionManager
import com.izubot.treinemais.data.local.TokenManager
import com.izubot.treinemais.data.remote.api.AuthApi
import com.izubot.treinemais.data.remote.dto.RefreshTokenRequest
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route


class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    @param:Named("AuthApiNoAuth") private val authApi: Lazy<AuthApi>,
    private val sessionManager: SessionManager
): Authenticator {

    /**
     * Attempts to obtain a fresh access token and rebuild the original request with an updated Authorization header.
     *
     * Verification to prevent infinite loops by checking the attempts and whether the route is the one that generates the refresh token.
     *
     * If another thread already updated the token, the method retries the request with that token. If no updated token
     * is available, it uses the refresh token to request new tokens, saves them, and returns the original request rebuilt
     * with the new access token. If a refresh is not possible or fails, clears stored tokens and triggers session expiration.
     *
     * @param route The connection route that triggered the authentication (may be null).
     * @param response The HTTP response that caused authentication to be attempted.
     * @return A new Request with an updated `Authorization: Bearer <token>` header, or `null` if a refreshed token cannot be obtained.
     */
    override fun authenticate(route: Route?, response: Response): Request? {

        if (responseCount(response) >= 2) return null

        if (response.request.url.encodedPath.endsWith("/auth/refresh")) return null

        val currentToken = tokenManager.getAccessToken()

        synchronized(this) {
            val updatedToken = tokenManager.getAccessToken()

            // Se o token já foi atualizado por outra thread, tenta a requisição com o novo token
            if (updatedToken != null && updatedToken != currentToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $updatedToken")
                    .build()
            }

            val refreshToken = tokenManager.getRefreshToken() ?:  run {
                tokenManager.clearTokens()
                sessionManager.triggerSessionExpired()
                return null
            }

            return try {
                val refreshTokenRequest = RefreshTokenRequest(refreshToken)

                val tokenResponse = runBlocking {
                    withTimeout(30_000L) { // 30 segundos
                        authApi.get().refresh(refreshTokenRequest)
                    }
                }

                // Salva os novos tokens no SharedPreferences
                tokenManager.saveTokens(
                    accessToken = tokenResponse.accessToken,
                    refreshToken = tokenResponse.refreshToken
                )

                // Retorna a requisição original com o novo token
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                    .build()
            } catch (e: Exception) {
                Log.e("TokenAuthenticator", "Erro ao atualizar token", e)
                
                // Refresh token falhou ou expirou:
                tokenManager.clearTokens()
                sessionManager.triggerSessionExpired()
                
                null
            }
        }
    }
}

private fun responseCount(response: Response): Int {
    var count = 1
    var prior = response.priorResponse

    while (prior != null) {
        count++
        prior = prior.priorResponse
    }

    return count
}