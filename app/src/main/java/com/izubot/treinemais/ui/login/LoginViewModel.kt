package com.izubot.treinemais.ui.login

import androidx.lifecycle.ViewModel
import com.izubot.treinemais.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

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
}
