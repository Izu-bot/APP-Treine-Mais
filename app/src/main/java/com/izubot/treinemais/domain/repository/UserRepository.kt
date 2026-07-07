package com.izubot.treinemais.domain.repository

import com.izubot.treinemais.data.local.entities.SyncStatus
import com.izubot.treinemais.data.local.entities.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun insertOrUpdateUser(user: User)
    suspend fun deleteUserById(id: String)
    suspend fun saveProfileImage(uri: String)
    suspend fun getUnsyncedData(status: SyncStatus): User?
}
