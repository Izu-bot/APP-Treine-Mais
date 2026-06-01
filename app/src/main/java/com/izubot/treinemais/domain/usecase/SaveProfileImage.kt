package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.data.repository.UserRepositoryImpl
import javax.inject.Inject

class SaveProfileImage @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl
) {
    suspend operator fun invoke(uri: String) {
        userRepositoryImpl.saveProfileImage(uri)
    }
}