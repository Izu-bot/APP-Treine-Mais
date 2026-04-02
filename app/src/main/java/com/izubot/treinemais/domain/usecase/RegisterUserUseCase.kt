package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.data.local.dto.UserLocalDto
import com.izubot.treinemais.data.local.dto.toDomain
import com.izubot.treinemais.data.local.entities.SyncStatus
import com.izubot.treinemais.data.repository.UserRepository
import com.izubot.treinemais.domain.model.RegisterRequest
import com.izubot.treinemais.domain.model.User
import com.izubot.treinemais.domain.repository.AuthRepository
import com.izubot.treinemais.ui.register.RegisterUiState
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uiState: RegisterUiState): Result<User> {
        val request = RegisterRequest(
            email = uiState.email,
            password = uiState.password,
            name = uiState.name,
            gender = uiState.selectedGender?.name ?: "",
            goals = uiState.selectedGoals?.name ?: ""
        )

        val result = authRepository.register(request)

        return result.onSuccess { user ->
            val userLocal = UserLocalDto(
                guidUser = user.userId,
                name = uiState.name,
                email = uiState.email,
                gender = uiState.selectedGender?.name ?: "",
                birthDate = uiState.birthDate.toString(),
                lastUpdatedAt = System.currentTimeMillis(),
                isDirty = true,
                syncStatus = SyncStatus.SYNCED
            )
            userRepository.insertOrUpdateUser(userLocal.toDomain())
        }
    }
}