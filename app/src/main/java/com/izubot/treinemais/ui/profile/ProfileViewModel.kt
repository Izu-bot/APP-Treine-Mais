package com.izubot.treinemais.ui.profile

import androidx.lifecycle.ViewModel
import com.izubot.treinemais.ui.register.Gender
import com.izubot.treinemais.ui.register.Goals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    fun onChangeName(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun onChangeEmail(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onChangeGender(gender: Gender) {
        _state.update { it.copy(gender = gender) }
    }

    fun onChangeGoals(goals: Goals) {
        _state.update { it.copy(goals = goals) }
    }

    fun onDismissDatePicker() {
        _state.update { it.copy(showDatePicker = false) }
    }

    fun onShowDatePicker() {
        _state.update { it.copy(showDatePicker = true) }
    }

    fun onDateSelected(date: LocalDate) {
        _state.update { it.copy(birthDate = date) }
    }

    fun onSwitchTheme() {
        _state.update { it.copy(themeCheck = !it.themeCheck) }
    }

    fun onSwitchNotification() {
        _state.update { it.copy(notificationCheck = !it.notificationCheck) }
    }

    fun onSwitchAiMode() {
        _state.update { it.copy(isAiEnabled = !it.isAiEnabled) }
    }
}