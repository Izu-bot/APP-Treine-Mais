package com.izubot.treinemais.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "Exercise",
    foreignKeys = [ForeignKey(
        entity = TrainingEntity::class,
        parentColumns = ["id"],
        childColumns = ["training_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("training_id")]
)
data class ExerciseEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "training_id", defaultValue = "") val trainingId: String,
    @ColumnInfo val name: String,
    @ColumnInfo val sets: Int,
    @ColumnInfo val reps: Int,
    @ColumnInfo val weight: Int,
    @ColumnInfo val description: String?
)
