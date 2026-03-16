package com.izubot.treinemais.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.abstraction.ValidationResult
import com.izubot.treinemais.domain.repository.AuthRepository
import com.izubot.treinemais.domain.usecase.ConfirmEmailUseCase
import com.izubot.treinemais.domain.usecase.LoginUseCase
import com.izubot.treinemais.domain.usecase.ValidateEmailUseCase
import com.izubot.treinemais.domain.usecase.ValidatePasswordConfirmationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val confirmEmailUseCase: ConfirmEmailUseCase,
    private val loginUseCase: LoginUseCase,
    private val validatePasswordConfirmationUseCase: ValidatePasswordConfirmationUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase
) : ViewModel() {

    private val _toastEvent = Channel<String>()
    val toastEvent = _toastEvent.receiveAsFlow()

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onValidatePassword() : Boolean {
        val result = validatePasswordConfirmationUseCase(_state.value.password, _state.value.password)

        _state.update {
            it.copy(
                passwordError = result is ValidationResult.Error,
                errorPasswordMessage = (result as? ValidationResult.Error)?.message,
            )
        }
        return result is ValidationResult.Success

    }

    fun onValidateEmail() : Boolean {
        val result = validateEmailUseCase(_state.value.email)

        _state.update {
            it.copy(
                emailError = result is ValidationResult.Error,
                errorEmailMessage = (result as? ValidationResult.Error)?.message
            )
        }

        return result is ValidationResult.Success
    }


    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            delay(1500)
            loginUseCase(_state.value)
                .onSuccess { user ->
                    Log.d("Login", "Logado com sucesso: $user")
                }
                .onFailure { error ->
                    Log.d("Login", "Não foi possivel logar: ${error.message}")
                }

            _state.update { it.copy(isLoading = false) }
        }
    }

    fun confirmEmail(token: String) {
        viewModelScope.launch {
            confirmEmailUseCase(token)
                .onSuccess {
                    _toastEvent.send("Email confirmado")
                }
                .onFailure { error ->
                    Log.d("Login", "$error")
                    _toastEvent.send("Falha ao confirmar o email")
                }
        }
    }
}
