package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.domain.repository.PrefsRepository
import javax.inject.Inject

class SaveAiUseCase @Inject constructor(
    private val repository: PrefsRepository
) {
    suspend operator fun invoke(isEnabled: Boolean) = repository.saveAi(isEnabled)
}
