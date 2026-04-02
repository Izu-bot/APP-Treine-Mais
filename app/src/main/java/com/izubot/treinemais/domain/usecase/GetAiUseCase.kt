package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.domain.repository.PrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetAiUseCase @Inject constructor(
    private val repository: PrefsRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.getAi()
    
    val aiCache: StateFlow<Boolean> = repository.aiCache
}
