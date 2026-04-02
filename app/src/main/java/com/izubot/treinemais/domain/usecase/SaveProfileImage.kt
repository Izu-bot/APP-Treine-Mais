package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveProfileImage @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uri: String) {
        userRepository.saveProfileImage(uri)
    }
}