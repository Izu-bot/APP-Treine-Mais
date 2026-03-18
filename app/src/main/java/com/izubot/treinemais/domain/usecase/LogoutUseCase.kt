package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.data.local.TokenManager
import com.izubot.treinemais.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) {
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