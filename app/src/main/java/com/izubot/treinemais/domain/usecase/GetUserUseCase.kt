package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.data.local.entities.User
import com.izubot.treinemais.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User?> = userRepository.getUser()
}