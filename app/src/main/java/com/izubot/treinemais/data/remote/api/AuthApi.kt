package com.izubot.treinemais.data.remote.api

import com.izubot.treinemais.data.remote.dto.LoginRequestDto
import com.izubot.treinemais.data.remote.dto.RefreshTokenRequest
import com.izubot.treinemais.data.remote.dto.RegisterRequestDto
import com.izubot.treinemais.data.remote.dto.TokenDto
import com.izubot.treinemais.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/auth/register")
    suspend fun register(@Body body: RegisterRequestDto) : UserDto

    /**
     * Confirms a user's email address using the provided confirmation token.
     *
     * @param token The confirmation token that was sent to the user's email. 
     */
    @GET("/auth/confirm-email")
    suspend fun confirmEmail(@Query("token") token: String)

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param body The user's login credentials.
     * @return A [TokenDto] containing authentication tokens.
     */
    @POST("/auth/login")
    suspend fun login(@Body body: LoginRequestDto) : TokenDto

    /**
     * Requests a new access token using a refresh token.
     *
     * @param body The refresh token payload sent in the request body.
     * @return A [TokenDto] containing the refreshed access (and refresh) tokens. 
     */
    @POST("/auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenRequest) : TokenDto
    /**
     * Invalidates a refresh token to end the user's session on the server.
     *
     * @param body Request payload containing the refresh token to be invalidated.
     * @return The server's response message as a `String`.
     */
    @POST("auth/logout")
    suspend fun logout(@Body body: RefreshTokenRequest) : String
}