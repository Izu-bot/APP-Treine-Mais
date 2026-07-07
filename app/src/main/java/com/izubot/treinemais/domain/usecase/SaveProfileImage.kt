package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.domain.repository.UserRepository
import javax.inject.Inject

class SaveProfileImage @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uri: String) {
        userRepository.saveProfileImage(uri)
    }
}