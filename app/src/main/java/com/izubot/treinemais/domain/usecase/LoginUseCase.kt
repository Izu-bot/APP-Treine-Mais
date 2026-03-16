package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.domain.model.LoginRequest
import com.izubot.treinemais.domain.model.Token
import com.izubot.treinemais.domain.repository.AuthRepository
import com.izubot.treinemais.ui.login.LoginState
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(uiState: LoginState): Result<Token> {
        val request = LoginRequest(
            email = uiState.email,
            password = uiState.password
        )

        return authRepository.login(request)
    }
}