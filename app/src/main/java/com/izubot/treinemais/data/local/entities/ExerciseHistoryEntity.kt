package com.izubot.treinemais.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_history",
    foreignKeys = [
        ForeignKey(
            entity = TrainingHistoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["training_history_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("training_history_id")])
data class ExerciseHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "training_history_id") val trainingHistoryId: Long = 0,
    val exerciseId: String,
    val exerciseName: String,
    val weight: Double,
    val reps: Int,
    val sets: Int
)