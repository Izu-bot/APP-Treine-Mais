package com.izubot.treinemais.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
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

    private fun checkPassword() {
        if (_state.value.password.isBlank()) {
            _state.update { it.copy(isPasswordError = true, passwordError = R.string.login_password_error) }
        }
    }

    private fun checkEmail() {
        if (_state.value.email.isBlank()) {
            _state.update { it.copy(isEmailError = true, emailError = R.string.login_email_error) }
        }
    }


    fun onLoginClick() {
        checkEmail()
        checkPassword()
    }

    fun confirmEmail(token: String) {
        viewModelScope.launch {
            repository.confirmEmail(token)
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
