package com.izubot.treinemais.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.izubot.treinemais.data.local.entities.ExerciseEntity
import com.izubot.treinemais.data.local.entities.TrainingEntity
import com.izubot.treinemais.data.local.entities.TrainingWithExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraining(training: TrainingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Delete
    suspend fun deleteTraining(training: TrainingEntity)

    @Transaction
    suspend fun insertTrainingWithExercises(training: TrainingEntity, exercises: List<ExerciseEntity>) {
        insertTraining(training)
        exercises.forEach { exercise ->
            insertExercise(exercise)
        }
    }

    @Transaction
    @Query("SELECT * FROM training")
    suspend fun getTrainingsWithExercises(): List<TrainingWithExercise>

    @Transaction
    @Query("SELECT * FROM training")
    fun getTrainingsWithExercisesFlow(): Flow<List<TrainingWithExercise>>
}