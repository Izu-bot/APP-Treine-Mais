package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.data.local.entities.User
import com.izubot.treinemais.data.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl
) {
    operator fun invoke(): Flow<User?> = userRepositoryImpl.getUser()
}