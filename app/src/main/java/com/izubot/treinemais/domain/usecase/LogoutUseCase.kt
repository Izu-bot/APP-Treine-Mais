package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.data.local.TokenManager
import com.izubot.treinemais.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) {
    /**
     * Performs a logout using the stored refresh token and clears local tokens afterwards.
     *
     * If no refresh token is available, the function short-circuits and returns a successful result
     * indicating that the refresh token was not found.
     *
     * @return A `Result<String>` containing the logout response message; if no refresh token was
     * found, returns `Result.success("Refresh token not found")`.
     */
    suspend operator fun invoke(): Result<String> {
        val refreshToken = tokenManager.getRefreshToken()

        return try {
            if (refreshToken.isNullOrBlank()) {
                Result.success("Refresh token not found")
            } else {
                authRepository.logout(refreshToken)
            }
        } finally {
            tokenManager.clearTokens()
        }
    }
}