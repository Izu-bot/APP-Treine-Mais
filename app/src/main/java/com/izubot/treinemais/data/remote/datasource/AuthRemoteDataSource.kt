package com.izubot.treinemais.data.remote.datasource

import com.izubot.treinemais.domain.model.LoginRequest
import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.Token
import com.izubot.treinemais.domain.model.User

interface AuthRemoteDataSource {
    /**
 * Creates a new user account using the provided registration details.
 *
 * @param request Registration data such as name, email, password, and any other required profile fields.
 * @return The created `User` representing the newly registered account.
 */
suspend fun register(request: RegisterRequest): User
    /**
 * Confirms a user's email address using an email confirmation token.
 *
 * Activates the user's account associated with the provided token.
 *
 * @param token The email confirmation token previously issued to the user.
 */
suspend fun confirmEmail(token: String)
    /**
 * Authenticates a user using the credentials in the provided request.
 *
 * @param request The login credentials (e.g., email/username and password).
 * @return A `Token` containing authentication credentials such as access and refresh tokens.
 */
suspend fun login(request: LoginRequest): Token
    /**
 * Obtain a new authentication token pair using a refresh token.
 *
 * @param refreshToken The refresh token previously issued for the user session.
 * @return A Token containing refreshed access (and refresh) token values and related metadata.
 */
suspend fun refreshToken(refreshToken: String): Token
    /**
 * Logs out the current user by revoking the provided refresh token.
 *
 * @param refreshToken The refresh token to revoke for ending the session.
 * @return The server's confirmation message. 
 */
suspend fun logout(refreshToken: String): String
}