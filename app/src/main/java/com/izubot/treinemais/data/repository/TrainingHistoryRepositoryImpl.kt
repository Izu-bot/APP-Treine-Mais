package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.local.dao.TrainingHistoryDao
import com.izubot.treinemais.data.local.entities.TrainingHistoryEntity
import com.izubot.treinemais.domain.model.DayProgress
import com.izubot.treinemais.domain.repository.TrainingHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class TrainingHistoryRepositoryImpl @Inject constructor(
    private val historyDao: TrainingHistoryDao
) : TrainingHistoryRepository {

    override fun getBetweenDates(
        startDate: String,
        endDate: String
    ): Flow<List<DayProgress>> {
        val today = LocalDate.now()
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        return historyDao.getHistoryBetweenDates(startDate, endDate)
            .map { history ->
                val daysCount = ChronoUnit.DAYS.between(start, end.plusDays(1))

                (0 until daysCount).map { i ->
                    val date = start.plusDays(i)
                    DayProgress(
                        date = date,
                        isCompleted = history.any { it.date == date.toString() },
                        isToday = date == today
                    )
                }
            }
    }

    override suspend fun markDayAsCompleted(date: String) {
        withContext(Dispatchers.IO) {
            historyDao.insertHistory(TrainingHistoryEntity(date = date, isCompleted = true))
        }
    }
}