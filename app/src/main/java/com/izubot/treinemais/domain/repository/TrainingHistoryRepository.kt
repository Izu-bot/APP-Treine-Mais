package com.izubot.treinemais.domain.repository

import com.izubot.treinemais.domain.model.DayProgress
import kotlinx.coroutines.flow.Flow

interface TrainingHistoryRepository {
    fun getBetweenDates(startDate: String, endDate: String): Flow<List<DayProgress>>
    suspend fun markDayAsCompleted(date: String)
}