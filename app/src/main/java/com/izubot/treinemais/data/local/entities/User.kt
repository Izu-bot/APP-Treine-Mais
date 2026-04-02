package com.izubot.treinemais.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user",
    indices = [Index(value = ["guid_user"], unique = true)]
)
data class User(
    @PrimaryKey val id: String = "default_user_id",
    @ColumnInfo(name = "guid_user") val guidUser: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "birth_date") val birthDate: String,
    @ColumnInfo(name = "last_updated_at") val lastUpdatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_dirty") val isDirty: Boolean = false,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.PENDING,
    @ColumnInfo(name = "local_photo_path") val localPhotoPath: String? = null
)