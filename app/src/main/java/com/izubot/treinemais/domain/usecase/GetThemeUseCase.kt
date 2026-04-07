package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.domain.repository.PrefsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.StateFlow

class GetThemeUseCase @Inject constructor(
    private val repository: PrefsRepository
) {
    operator fun invoke(): StateFlow<Boolean> = repository.themeCache
}