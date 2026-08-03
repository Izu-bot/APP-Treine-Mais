package com.izubot.treinemais.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.izubot.treinemais.data.local.dto.WeightEntry
import com.izubot.treinemais.data.local.entities.ExerciseHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseHistoryDao {
    @Query(
        """
            SELECT eh.weight, th.date, eh.reps
            FROM exercise_history eh
            INNER JOIN training_history th ON eh.training_history_id = th.id
            WHERE eh.exerciseId = :exerciseId
            ORDER BY th.timestamp ASC
        """
    )
    fun getWeightEvolution(exerciseId: String): Flow<List<WeightEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseTraining(exerciseTraining: ExerciseHistoryEntity)
}