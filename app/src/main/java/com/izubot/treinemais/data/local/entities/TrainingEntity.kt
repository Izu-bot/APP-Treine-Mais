package com.izubot.treinemais.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Training",
)
data class TrainingEntity (
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
    @ColumnInfo val description: String?
)