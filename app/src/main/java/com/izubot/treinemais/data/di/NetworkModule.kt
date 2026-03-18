package com.izubot.treinemais.data.di

import android.content.Context
import com.izubot.treinemais.data.local.TokenManager
import com.izubot.treinemais.data.remote.api.AuthApi
import com.izubot.treinemais.data.remote.interceptors.AuthInterceptor
import com.izubot.treinemais.data.remote.interceptors.TokenAuthenticator
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val URL = "http://10.0.2.2:5297"

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    /**
     * Provides a singleton OkHttpClient configured for authentication and HTTP logging.
     *
     * The client is configured with the supplied AuthInterceptor and TokenAuthenticator, and an
     * HttpLoggingInterceptor that logs request and response bodies.
     *
     * @param authInterceptor Interceptor that adds authentication information (e.g., headers) to requests.
     * @param tokenAuthenticator Authenticator that handles token refreshing and retrying failed auth requests.
     * @return An OkHttpClient configured with the authentication interceptor, authenticator, and body-level HTTP logging.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    @Named("NoAuthClient")
    fun provideNoAuthOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    /**
     * Creates an AuthApi implementation from the provided Retrofit instance.
     *
     * @return An AuthApi implementation created by Retrofit.
     */
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    /**
     * Provides a TokenManager initialized with the application context.
     *
     * @param context The application Context used to construct the TokenManager.
     * @return A TokenManager instance configured with the provided context.
     */
    @Provides
    @Singleton
    @Named("AuthApiNoAuth")
    fun provideAuthApiNoAuth(
        @Named("NoAuthClient") okHttpClient: OkHttpClient,
        json: Json
    ): AuthApi {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }
}
