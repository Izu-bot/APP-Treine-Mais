package com.izubot.treinemais.ui.register

import androidx.lifecycle.ViewModel
import com.izubot.treinemais.domain.usecase.ValidatePasswordConfirmationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val validatePasswordConfirmationUseCase = ValidatePasswordConfirmationUseCase()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = false) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, passwordError = false) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisibility = !it.passwordVisibility) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordVisibility = !it.confirmPasswordVisibility) }
    }

    fun onNextStep() {
        if (_uiState.value.currentStep == 0) {
            val passwordsMatch = validatePasswordConfirmationUseCase(
                password = _uiState.value.password,
                confirmPassword = _uiState.value.confirmPassword
            )
            if (!passwordsMatch) {
                _uiState.update { it.copy(passwordError = true) }
                return
            }
        }

        if (_uiState.value.currentStep < _uiState.value.totalSteps - 1) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        } else {
            // TODO: Finalize registration
        }
    }

    fun onPreviousStep() {
        if (_uiState.value.currentStep > 0) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }
}