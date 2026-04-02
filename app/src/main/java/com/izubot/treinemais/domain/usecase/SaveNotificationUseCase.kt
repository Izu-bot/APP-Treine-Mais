package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.domain.repository.PrefsRepository
import jakarta.inject.Inject

class SaveNotificationUseCase @Inject constructor(
    private val repository: PrefsRepository
) {
    suspend operator fun invoke(isEnabled: Boolean) = repository.saveNotification(isEnabled)
}