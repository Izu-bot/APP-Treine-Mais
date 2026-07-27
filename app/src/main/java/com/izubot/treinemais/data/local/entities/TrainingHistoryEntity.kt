package com.izubot.treinemais.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_history")
data class TrainingHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    val id: Long = 0,
    @ColumnInfo(defaultValue = "")
    val trainingId: String,
    val date: String,
    @ColumnInfo(defaultValue = "0")
    val timestamp: Long,
    val isCompleted: Boolean = true
)
