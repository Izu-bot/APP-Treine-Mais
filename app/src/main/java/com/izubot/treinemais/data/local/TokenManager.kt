package com.izubot.treinemais.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "treine_mais_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    /**
     * Stores the access and refresh tokens in the encrypted shared preferences, overwriting any existing values.
     *
     * @param accessToken The access token to store.
     * @param refreshToken The refresh token to store.
     */
    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    /**
 * Retrieves the stored access token from the encrypted preferences.
 *
 * @return The access token string if present, `null` otherwise.
 */
fun getAccessToken(): String? = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

    /**
 * Retrieves the stored refresh token from encrypted shared preferences.
 *
 * @return The refresh token, or `null` if none is stored.
 */
fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    /**
     * Removes all entries from the encrypted shared preferences, including the stored access and refresh tokens.
     */
    fun clearTokens() {
        sharedPreferences.edit { clear() }
    }
}