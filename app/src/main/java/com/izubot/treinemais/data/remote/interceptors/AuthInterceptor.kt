package com.izubot.treinemais.data.remote.interceptors

import com.izubot.treinemais.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenManager.getAccessToken()

        val request = if (
            accessToken.isNullOrBlank() ||
            originalRequest.header("Authorization") != null
            ) {
            originalRequest
        } else {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        }
            return chain.proceed(request)
    }
}