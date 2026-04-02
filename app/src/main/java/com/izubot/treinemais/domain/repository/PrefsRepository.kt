package com.izubot.treinemais.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PrefsRepository {

    suspend fun saveTheme(isTheme: Boolean): Result<Unit>
    fun getTheme(): Flow<Boolean>
    val themeCache: StateFlow<Boolean>

    suspend fun saveNotification(isEnabled: Boolean): Result<Unit>
    fun getNotification(): Flow<Boolean>
    val notificationCache: StateFlow<Boolean>

    suspend fun saveAi(isEnabled: Boolean): Result<Unit>
    fun getAi(): Flow<Boolean>
    val aiCache: StateFlow<Boolean>
    
    suspend fun preload()
}