package com.izubot.treinemais.data.local.datasource

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "treine_mais_secure_prefs")

class DataStorePrefs(
    private val context: Context,
    scope: CoroutineScope
) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val THEME_MODE = booleanPreferencesKey("theme_mode")
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        private val AI_ENABLED = booleanPreferencesKey("ai_enabled")
    }

    private val aead: Aead by lazy {
        AeadConfig.register()
        AndroidKeysetManager.Builder()
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withSharedPref(context, "token_keyset", "token_keyset_prefs")
            .withMasterKeyUri("android-keystore://token_master_key")
            .build()
            .keysetHandle
            .getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN] != null && prefs[REFRESH_TOKEN] != null
    }

    val tokens: Flow<Pair<String?, String?>> = context.dataStore.data.map { prefs ->
        val access = prefs[ACCESS_TOKEN]?.decrypt()
        val refresh = prefs[REFRESH_TOKEN]?.decrypt()
        access to refresh
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { pref ->
            pref[ACCESS_TOKEN] = accessToken.encrypt()
            pref[REFRESH_TOKEN] = refreshToken.encrypt()
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { pref ->
            pref.remove(ACCESS_TOKEN)
            pref.remove(REFRESH_TOKEN)
        }
    }

    private fun String.encrypt(): String {
        val encrypted = aead.encrypt(toByteArray(), null)
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    private fun String.decrypt(): String? {
        return try {
            val decoded = Base64.decode(this, Base64.NO_WRAP)
            String(aead.decrypt(decoded, null))
        } catch (_: Exception) {
            null
        }
    }

    val themeCache: StateFlow<Boolean> = context.dataStore.data
        .map { it[THEME_MODE] ?: false }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    val notificationCache: StateFlow<Boolean> = context.dataStore.data
        .map { it[NOTIFICATION_ENABLED] ?: false }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    val aiCache: StateFlow<Boolean> = context.dataStore.data
        .map { it[AI_ENABLED] ?: false }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)
    suspend fun saveThemePrefs(themeMode: Boolean) {
        context.dataStore.edit { it[THEME_MODE] = themeMode }
    }

    suspend fun saveNotificationPref(isEnabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATION_ENABLED] = isEnabled }
    }

    suspend fun saveAiPref(isEnabled: Boolean) {
        context.dataStore.edit { it[AI_ENABLED] = isEnabled }
    }

}