package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.data.local.TokenManager
import com.izubot.treinemais.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): Result<String> {
        val refreshToken = tokenManager.getRefreshToken() ?: return Result.success(("Refresh token not found"))

        return authRepository.logout(refreshToken).also {
            tokenManager.clearTokens()
        }
    }
}