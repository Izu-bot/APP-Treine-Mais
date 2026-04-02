package com.izubot.treinemais.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.izubot.treinemais.data.local.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<User?>

    @Upsert
    suspend fun insertOrUpdateUser(user: User)

    @Query("DELETE FROM user WHERE id = :id")
    suspend fun deleteUserById(id: String)
}