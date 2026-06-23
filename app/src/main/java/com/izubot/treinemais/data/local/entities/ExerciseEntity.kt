package com.izubot.treinemais.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "Exercise",
    indices = [Index(value = ["id"], unique = true)]
)
data class ExerciseEntity(
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
    @ColumnInfo val sets: Int,
    @ColumnInfo val reps: Int,
    @ColumnInfo val description: String?
)