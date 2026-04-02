package com.izubot.treinemais.data.di

import android.content.Context
import com.izubot.treinemais.data.local.datasource.DataStorePrefs
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
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val URL = "http://10.0.2.2:5297/"

    /**
     * Provides a singleton Json instance configured for serialization.
     *
     * @return A Json instance with ignoreUnknownKeys and coerceInputValues enabled.
     */
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
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    /**
     * Provides an OkHttpClient without authentication interceptors or authenticators.
     *
     * This client is intended for requests that should not include or trigger authentication
     * logic, such as initial login or token refresh calls.
     *
     * @return An OkHttpClient without authentication logic, including body-level HTTP logging.
     */
    @Provides
    @Singleton
    @Named("NoAuthClient")
    fun provideNoAuthOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Provides a singleton Retrofit instance configured with the default OkHttpClient and Json converter.
     *
     * @param okHttpClient The default OkHttpClient with authentication.
     * @param json The Json instance for serialization.
     * @return A Retrofit instance configured for the application's base URL.
     */
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
     * @param retrofit The default Retrofit instance.
     * @return An AuthApi implementation created by Retrofit.
     */
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    /**
     * Provides a specialized AuthApi instance for authentication and token refresh operations
     * that does not use the default AuthInterceptor or TokenAuthenticator.
     *
     * @param okHttpClient The OkHttpClient without authentication interceptors.
     * @param json The Json instance for serialization.
     * @return An AuthApi instance for no-auth requests.
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

    /**
     * Provides a TokenManager initialized with the application context.
     *
     * @param context The application Context used to construct the TokenManager.
     * @return A TokenManager instance configured with the provided context.
     */
    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): DataStorePrefs {
        return DataStorePrefs(context)
    }
}
