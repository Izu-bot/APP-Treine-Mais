package com.izubot.treinemais.ui.profile

import com.izubot.treinemais.ui.register.Gender
import com.izubot.treinemais.ui.register.Goals
import java.time.LocalDate

data class ProfileUiState(
    val name: String = "Kauan",
    val email: String = "kauan@gmail.com",
    val gender: String? = null,
    val goals: String? = null,
    val birthDate: LocalDate? = null,
    val showDatePicker: Boolean = false,
    val themeCheck: Boolean = false,
    val notificationCheck: Boolean = false,
    val isAiEnabled: Boolean = false,
    val imageUri: String = "",
    val isLoading: Boolean = false,
    val showDialog: Boolean = false,
    val isLogout: Boolean = false
)