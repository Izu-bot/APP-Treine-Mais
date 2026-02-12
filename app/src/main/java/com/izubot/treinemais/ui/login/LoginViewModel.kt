package com.izubot.treinemais.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

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

    fun onLoginClick() {
        // TODO: Implement login logic (e.g., call a repository/use case)
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // Simulate network call
            kotlinx.coroutines.delay(2000)
            // Handle success or error
            _state.update { it.copy(isLoading = false, loginSuccess = true) }
        }
    }
}
