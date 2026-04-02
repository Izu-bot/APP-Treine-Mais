package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.data.local.datasource.DataStorePrefs
import com.izubot.treinemais.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStorePrefs: DataStorePrefs
) {
    suspend operator fun invoke(): Result<String> {
        val refreshToken = dataStorePrefs.tokens.first().first

        return try {
            if (refreshToken.isNullOrBlank()) {
                Result.success("Refresh token not found")
            } else {
                authRepository.logout(refreshToken)
            }
        } finally {
            dataStorePrefs.clearTokens()
        }
    }
}