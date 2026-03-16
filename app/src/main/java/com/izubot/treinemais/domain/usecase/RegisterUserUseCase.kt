package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.User
import com.izubot.treinemais.domain.repository.AuthRepository
import com.izubot.treinemais.ui.register.RegisterUiState
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(uiState: RegisterUiState): Result<User> {
        val request = RegisterRequest(
            email = uiState.email,
            password = uiState.password,
            name = uiState.name,
            gender = uiState.selectedGender!!.name,
            goals = uiState.selectedGoals!!.name
        )

        return authRepository.register(request)
    }
}