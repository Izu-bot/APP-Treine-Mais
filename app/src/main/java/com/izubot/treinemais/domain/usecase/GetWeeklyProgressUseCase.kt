package com.izubot.treinemais.domain.usecase

import androidx.compose.animation.with
import com.izubot.treinemais.domain.model.DayProgress
import com.izubot.treinemais.domain.repository.TrainingHistoryRepository
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class GetWeeklyProgressUseCase @Inject constructor(
    private val trainingHistoryRepository: TrainingHistoryRepository
) {
    suspend operator fun invoke(): Flow<List<DayProgress>> {
        val today = LocalDate.now()

        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = startOfWeek.plusDays(6)

        return trainingHistoryRepository.getBetweenDates(
            startDate = startOfWeek.toString(),
            endDate = endOfWeek.toString()
        )
    }
}