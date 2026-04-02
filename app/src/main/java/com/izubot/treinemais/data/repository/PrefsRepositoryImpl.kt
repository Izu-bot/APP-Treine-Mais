package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.local.datasource.DataStorePrefs
import com.izubot.treinemais.domain.repository.PrefsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class PrefsRepositoryImpl @Inject constructor(
    private val dataStorePrefs: DataStorePrefs
) : PrefsRepository {
    override suspend fun saveTheme(isTheme: Boolean): Result<Unit> {
        return runCatching {
            dataStorePrefs.saveThemePrefs(isTheme)
        }
    }

    override fun getTheme(): Flow<Boolean> {
        return dataStorePrefs.getThemePref
    }

    override val themeCache: StateFlow<Boolean> = dataStorePrefs.themeCache

    override suspend fun saveNotification(isEnabled: Boolean): Result<Unit> {
        return runCatching {
            dataStorePrefs.saveNotificationPref(isEnabled)
        }
    }

    override fun getNotification(): Flow<Boolean> {
        return dataStorePrefs.getNotificationPref
    }

    override val notificationCache: StateFlow<Boolean> = dataStorePrefs.notificationCache

    override suspend fun saveAi(isEnabled: Boolean): Result<Unit> {
        return runCatching {
            dataStorePrefs.saveAiPref(isEnabled)
        }
    }

    override fun getAi(): Flow<Boolean> {
        return dataStorePrefs.getAiPref
    }

    override val aiCache: StateFlow<Boolean> = dataStorePrefs.aiCache

    override suspend fun preload() {
        dataStorePrefs.preload()
    }

}
