package com.izubot.treinemais.data.remote.interceptors

import com.izubot.treinemais.data.local.datasource.DataStorePrefs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStorePrefs: DataStorePrefs
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val accessToken = runBlocking { dataStorePrefs.tokens.first().first }

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