package com.izubot.treinemais.ui.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.domain.abstraction.ValidationResult
import com.izubot.treinemais.domain.usecase.RegisterUserUseCase
import com.izubot.treinemais.domain.usecase.ValidateEmailUseCase
import com.izubot.treinemais.domain.usecase.ValidateNameUseCase
import com.izubot.treinemais.domain.usecase.ValidatePasswordConfirmationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validatePasswordConfirmationUseCase: ValidatePasswordConfirmationUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validateNameUseCase: ValidateNameUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = false) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = false) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, passwordError = false) }
    }

    fun onNameChange(name: String) {
        _uiState.update {it.copy(name = name) }
    }

    fun onGenderSelected(gender: Gender) {
        _uiState.update { it.copy(selectedGender = gender) }
    }

    fun onGoalsSelected(goals: Goals) {
        _uiState.update { it.copy(selectedGoals = goals) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisibility = !it.passwordVisibility) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordVisibility = !it.confirmPasswordVisibility) }
    }

    fun onValidateName(): Boolean {
        val result = validateNameUseCase(_uiState.value.name)

        _uiState.update {
            it.copy( nameError = result is ValidationResult.Error, errorNameMessage = (result as? ValidationResult.Error)?.message )
        }

        return result is ValidationResult.Success
    }

    fun onValidateEmail(): Boolean {
        val result = validateEmailUseCase(_uiState.value.email)

        _uiState.update {
            it.copy(
                emailError = result is ValidationResult.Error,
                errorEmailMessage = (result as? ValidationResult.Error)?.message
            )
        }

        return result is ValidationResult.Success
    }


    fun onValidatePassword(): Boolean {
        val result = validatePasswordConfirmationUseCase(_uiState.value.password, _uiState.value.confirmPassword)

        _uiState.update {
            it.copy(
                passwordError = result is ValidationResult.Error,
                errorPasswordMessage = (result as? ValidationResult.Error)?.message,
            )
        }

        return result is ValidationResult.Success
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            registerUserUseCase(_uiState.value)
                .onSuccess { user ->
                    Log.d("Registrar", "Sucesso $user")
                }
                .onFailure { error ->
                    Log.d("Registrar", "Falha $error")
                    _uiState.update { it.copy(isError = true, errorMassage = error.message) }
                }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onNextStep() {
        if (_uiState.value.currentStep < _uiState.value.totalSteps - 1) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun onPreviousStep() {
        if (_uiState.value.currentStep > 0) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }
}
