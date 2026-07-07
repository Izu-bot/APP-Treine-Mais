package com.izubot.treinemais.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_history")
data class TrainingHistoryEntity(
    @PrimaryKey val date: String,
    val isCompleted: Boolean = true
)