package com.izubot.treinemais.ui.register

data class RegisterUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 3,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisibility: Boolean = false,
    val confirmPasswordVisibility: Boolean = false,
    val passwordError: Boolean = false,
    val name: String = "",
    val selectedGender: Gender? = null,
    val selectedGoals: Goals? = null
)