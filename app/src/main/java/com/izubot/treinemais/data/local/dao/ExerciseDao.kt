package com.izubot.treinemais.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.izubot.treinemais.data.local.entities.ExerciseEntity

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun getExerciseWithId(id: String): ExerciseEntity?

    @Query("SELECT * FROM exercise Where name = :name")
    suspend fun getExerciseWithName(name: String): ExerciseEntity?

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Upsert
    suspend fun upsertExercise(exercise: ExerciseEntity)

    @Query("SELECT * FROM exercise")
    suspend fun getAllExercises(): List<ExerciseEntity>
}