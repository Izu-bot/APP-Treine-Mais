package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.domain.repository.PrefsRepository
import javax.inject.Inject

class SaveThemeUseCase @Inject constructor(
    private val repository: PrefsRepository
) {
    suspend operator fun invoke(isDynamicTheme: Boolean) = repository.saveTheme(isDynamicTheme)
}