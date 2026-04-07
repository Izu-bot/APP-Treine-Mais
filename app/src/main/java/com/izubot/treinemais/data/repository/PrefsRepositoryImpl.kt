package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.local.datasource.DataStorePrefs
import com.izubot.treinemais.domain.repository.PrefsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.StateFlow

class PrefsRepositoryImpl @Inject constructor(
    private val dataStorePrefs: DataStorePrefs
) : PrefsRepository {
    override suspend fun saveTheme(isTheme: Boolean): Result<Unit> {
        return runCatching {
            dataStorePrefs.saveThemePrefs(isTheme)
        }
    }
    override val themeCache: StateFlow<Boolean> = dataStorePrefs.themeCache

    override suspend fun saveNotification(isEnabled: Boolean): Result<Unit> {
        return runCatching {
            dataStorePrefs.saveNotificationPref(isEnabled)
        }
    }
    override val notificationCache: StateFlow<Boolean> = dataStorePrefs.notificationCache

    override suspend fun saveAi(isEnabled: Boolean): Result<Unit> {
        return runCatching {
            dataStorePrefs.saveAiPref(isEnabled)
        }
    }
    override val aiCache: StateFlow<Boolean> = dataStorePrefs.aiCache

}
