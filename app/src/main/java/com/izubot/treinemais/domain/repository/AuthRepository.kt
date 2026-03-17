package com.izubot.treinemais.domain.repository

import com.izubot.treinemais.domain.model.LoginRequest
import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.Token
import com.izubot.treinemais.domain.model.User

interface AuthRepository {
    /**
 * Creates a new user account using the provided registration data.
 *
 * @param request The registration data for the new user (for example: name, email, password).
 * @return A Result containing the created User on success, or an error describing the failure.
 */
suspend fun register(request: RegisterRequest): Result<User>
    /**
 * Confirms a user's email using the provided confirmation token.
 *
 * @param token The email confirmation token sent to the user.
 * @return A Result containing `Unit` on success, or an error describing the failure.
 */
suspend fun confirmEmail(token: String): Result<Unit>
    /**
 * Authenticates a user with the provided credentials and obtains an access token.
 *
 * @param request The login credentials (e.g., email/username and password).
 * @return A `Result` containing a `Token` on success, or a failure describing the error.
 */
suspend fun login(request: LoginRequest): Result<Token>
    /**
 * Refreshes authentication credentials using the provided refresh token.
 *
 * @param refreshToken The refresh token previously issued by the authentication service.
 * @return A Result containing a Token with updated access and refresh tokens on success, or an error result on failure.
 */
suspend fun refreshToken(refreshToken: String): Result<Token>
    /**
 * Invalidates the provided refresh token and signs the user out.
 *
 * @param refreshToken The refresh token to invalidate.
 * @return A confirmation message string on successful logout.
 */
suspend fun logout(refreshToken: String): Result<String>
}