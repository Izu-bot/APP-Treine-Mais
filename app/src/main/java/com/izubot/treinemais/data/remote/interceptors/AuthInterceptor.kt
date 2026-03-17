package com.izubot.treinemais.data.remote.interceptors

import com.izubot.treinemais.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val requestBuilder = originRequest.newBuilder()
            .header("Authorization", "Bearer ${tokenManager.getAccessToken()}")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}