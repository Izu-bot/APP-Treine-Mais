package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.domain.repository.AuthRepository
import javax.inject.Inject

class ConfirmEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        return authRepository.confirmEmail(token)
    }
}