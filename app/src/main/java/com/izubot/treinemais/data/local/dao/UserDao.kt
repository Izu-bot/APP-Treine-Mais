package com.izubot.treinemais.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.izubot.treinemais.data.local.entities.SyncStatus
import com.izubot.treinemais.data.local.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<User?>

    @Upsert
    suspend fun insertOrUpdateUser(user: User)

    @Query("DELETE FROM user WHERE id = :id")
    suspend fun deleteUserById(id: String)

    @Query("SELECT * FROM user WHERE sync_status = :status LIMIT 1")
    suspend fun getUserWithSyncStatus(status: SyncStatus): User?

    @Query("UPDATE user SET sync_status = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}