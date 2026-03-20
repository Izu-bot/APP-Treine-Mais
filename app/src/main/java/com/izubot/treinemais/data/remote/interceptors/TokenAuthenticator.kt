package com.izubot.treinemais.data.remote.interceptors

import android.util.Log
import com.izubot.treinemais.data.local.SessionManager
import com.izubot.treinemais.data.local.TokenManager
import com.izubot.treinemais.data.remote.api.AuthApi
import com.izubot.treinemais.data.remote.dto.RefreshTokenRequest
import dagger.Lazy
import kotlinx.coroutines.flow.first
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

    override fun authenticate(route: Route?, response: Response): Request? {

        if (response.priorResponse != null) return null
        if (response.request.url.encodedPath.endsWith("/auth/refresh")) return null

        val currentToken = runBlocking { tokenManager.tokens.first().first }

        return synchronized(this) {
            runBlocking {
                val updatedToken = tokenManager.tokens.first().first

                if (updatedToken != null && updatedToken != currentToken) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $updatedToken")
                        .build()
                }

                val refreshToken = tokenManager.tokens.first().second ?: run {
                    tokenManager.clearTokens()
                    sessionManager.triggerSessionExpired()
                    return@runBlocking null
                }

                try {
                    val tokenResponse = withTimeout(30_000L) {
                        val apiResponse = authApi.get().refresh(RefreshTokenRequest(refreshToken))

                        tokenManager.saveTokens(
                            accessToken = apiResponse.accessToken,
                            refreshToken = apiResponse.refreshToken
                        )
                        apiResponse
                    }

                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                        .build()
                } catch (e: Exception) {
                    Log.e("TokenAuthenticator", "Erro ao atualizar token", e)
                    tokenManager.clearTokens()
                    sessionManager.triggerSessionExpired()
                    null
                }
            }
        }
    }
}