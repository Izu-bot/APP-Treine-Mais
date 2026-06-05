package com.izubot.treinemais.data.converter

import androidx.room.TypeConverter
import com.izubot.treinemais.data.local.entities.SyncStatus

class Converters {
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String {
        return status.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }
}