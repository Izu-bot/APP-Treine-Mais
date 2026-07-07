package com.izubot.treinemais.domain.model

import java.time.LocalDate

data class DayProgress(
    val date: LocalDate,
    val isCompleted: Boolean,
    val isToday: Boolean
)
