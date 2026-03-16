package com.izubot.treinemais.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.repository.AuthRepository
import com.izubot.treinemais.domain.usecase.ConfirmEmailUseCase
import com.izubot.treinemais.domain.usecase.LoginUseCase
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
    private val loginUseCase: LoginUseCase
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

    private fun onValidatePassword() {
        // Por o Usecase de validação
    }

    private fun onValidateEmail() {
        // Por o Usecase de validação
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
