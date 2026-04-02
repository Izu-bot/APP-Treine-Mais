package com.izubot.treinemais.data.remote.interceptors

import android.util.Log
import com.izubot.treinemais.data.local.helpers.SessionManager
import com.izubot.treinemais.data.local.datasource.DataStorePrefs
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
    private val dataStorePrefs: DataStorePrefs,
    @param:Named("AuthApiNoAuth") private val authApi: Lazy<AuthApi>,
    private val sessionManager: SessionManager
): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (response.priorResponse != null) return null
        if (response.request.url.encodedPath.endsWith("/auth/refresh")) return null

        val currentToken = runBlocking { dataStorePrefs.tokens.first().first }

        return synchronized(this) {
            runBlocking {
                val updatedToken = dataStorePrefs.tokens.first().first

                if (updatedToken != null && updatedToken != currentToken) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $updatedToken")
                        .build()
                }

                val refreshToken = dataStorePrefs.tokens.first().second ?: run {
                    dataStorePrefs.clearTokens()
                    sessionManager.triggerSessionExpired()
                    return@runBlocking null
                }

                try {
                    val tokenResponse = withTimeout(30_000L) {
                        val apiResponse = authApi.get().refresh(RefreshTokenRequest(refreshToken))

                        dataStorePrefs.saveTokens(
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
                    dataStorePrefs.clearTokens()
                    sessionManager.triggerSessionExpired()
                    null
                }
            }
        }
    }
}