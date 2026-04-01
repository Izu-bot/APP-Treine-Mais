package com.izubot.treinemais.ui.profile

import com.izubot.treinemais.ui.register.Gender
import com.izubot.treinemais.ui.register.Goals
import java.time.LocalDate

data class ProfileUiState(
    val name: String = "Kauan Martins Cardoso",
    val email: String = "kauan@gmail.com",
    val gender: Gender? = null,
    val goals: Goals? = null,
    val birthDate: LocalDate? = null,
    val showDatePicker: Boolean = false,
    val themeCheck: Boolean = false,
    val notificationCheck: Boolean = false,
    val isAiEnabled: Boolean = false
)