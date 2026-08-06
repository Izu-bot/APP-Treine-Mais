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

    @Query("SELECT MAX(weight) FROM exercise_history WHERE exerciseId = :exerciseId")
    suspend fun maxWeightByExercise(exerciseId: String): Double

    @Query("""
        SELECT SUM(weight * reps * sets)
        FROM exercise_history
        WHERE training_history_id = (
            SELECT training_history_id
            FROM exercise_history
            WHERE exerciseId = :exerciseId
            ORDER BY id DESC LIMIT 1
        ) AND exerciseId = :exerciseId
    """)
    suspend fun getLastExerciseVolume(exerciseId: String): Double

    @Query("""
        SELECT MAX(session_volume) FROM (
            SELECT SUM(weight * reps * sets) AS session_volume
            FROM exercise_history
            WHERE exerciseId = :exerciseId
            GROUP BY training_history_id
        )
    """)
    suspend fun getExerciseVolumeRecord(exerciseId: String): Double

    @Query("""
        SELECT SUM(eh.weight * eh.reps * eh.sets)
        FROM exercise_history eh
        INNER JOIN training_history th ON eh.training_history_id = th.id
        WHERE th.date >= :startDate AND th.date <= :endDate
    """)
    suspend fun getTotalVolumeBetweenDates(startDate: String, endDate: String): Double

    @Query("""
        SELECT SUM(eh.weight * eh.reps * eh.sets)
        FROM exercise_history eh
        WHERE eh.training_history_id = (
            SELECT id FROM training_history 
            WHERE trainingId = :trainingId 
            ORDER BY timestamp DESC LIMIT 1
        )
    """)
    suspend fun getLastTrainingVolume(trainingId: String): Double

    @Query("""
        SELECT MAX(session_volume) FROM (
            SELECT SUM(eh.weight * eh.reps * eh.sets) as session_volume
            FROM exercise_history eh
            INNER JOIN training_history th ON eh.training_history_id = th.id
            WHERE th.trainingId = :trainingId
            GROUP BY th.id
        )
    """)
    suspend fun getTrainingVolumeRecord(trainingId: String): Double

    @Query("""
        SELECT SUM(eh.weight * eh.reps * eh.sets) as weight, th.date as date, 0 as reps
        FROM exercise_history eh
        INNER JOIN training_history th ON eh.training_history_id = th.id
        WHERE th.trainingId = :trainingId
        GROUP BY th.id
        ORDER BY th.timestamp ASC
    """)
    fun getTrainingVolumeEvolution(trainingId: String): Flow<List<WeightEntry>>
}