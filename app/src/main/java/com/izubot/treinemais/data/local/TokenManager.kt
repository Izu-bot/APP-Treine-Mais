package com.izubot.treinemais.data.local

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "treine_mais_secure_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    // Inicializa o Tink e cria/recupera a chave do Android Keystore
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
        Log.d("DataStore", "Checking access ${prefs[ACCESS_TOKEN]}\tChecking refresh ${prefs[REFRESH_TOKEN]}")
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
        context.dataStore.edit { it.clear() }
    }

    private fun String.encrypt(): String {
        val encrypted = aead.encrypt(toByteArray(), null)
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    private fun String.decrypt(): String {
        val decoded = Base64.decode(this, Base64.NO_WRAP)
        return String(aead.decrypt(decoded, null))
    }
}