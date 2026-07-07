package com.izubot.treinemais.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.izubot.treinemais.data.local.entities.TrainingHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingHistoryDao {
    @Query("SELECT * FROM training_history WHERE date >= :startDate AND date <= :endDate")
    fun getHistoryBetweenDates(startDate: String, endDate: String): Flow<List<TrainingHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: TrainingHistoryEntity)
}
