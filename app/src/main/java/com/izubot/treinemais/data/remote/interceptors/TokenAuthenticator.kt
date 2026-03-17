package com.izubot.treinemais.data.remote.interceptors

import android.util.Log
import com.izubot.treinemais.data.local.SessionManager
import com.izubot.treinemais.data.local.TokenManager
import com.izubot.treinemais.data.remote.api.AuthApi
import com.izubot.treinemais.data.remote.dto.RefreshTokenRequest
import dagger.Lazy
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route


class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: Lazy<AuthApi>,
    private val sessionManager: SessionManager
): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val currentToken = tokenManager.getAccessToken()

        synchronized(this) {
            val updatedToken = tokenManager.getAccessToken()

            // Se o token já foi atualizado por outra thread, tenta a requisição com o novo token
            if (updatedToken != null && updatedToken != currentToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $updatedToken")
                    .build()
            }

            val refreshToken = tokenManager.getRefreshToken() ?: return null

            return try {
                // Chamada síncrona para refresh token usando runBlocking
                val tokenResponse = runBlocking {
                    authApi.get().refresh(RefreshTokenRequest(refreshToken))
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
