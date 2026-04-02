package com.izubot.treinemais.ui.register

import androidx.annotation.StringRes
import java.time.LocalDate

data class RegisterUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 3,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isError: Boolean = false,
    val errorMassage: String? = "",
    @param:StringRes val errorPasswordMessage: Int? = null,
    @param:StringRes val errorEmailMessage: Int? = null,
    @param:StringRes val errorNameMessage: Int? = null,
    val isLoading: Boolean = false,
    val passwordVisibility: Boolean = false,
    val confirmPasswordVisibility: Boolean = false,
    val passwordError: Boolean = false,
    val emailError: Boolean = false,
    val name: String = "",
    val nameError: Boolean = false,
    val selectedGender: Gender? = null,
    val selectedGoals: Goals? = null,
    val enabledButton: Boolean = false,
    val birthDate: LocalDate? = null,
)