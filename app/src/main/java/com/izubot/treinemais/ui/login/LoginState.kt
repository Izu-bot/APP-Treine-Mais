package com.izubot.treinemais.ui.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val loginFailure: Boolean = false,
    val loginError: Int? = null,
    val isPasswordVisible: Boolean = false,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val emailError: Int? = null,
    val passwordError: Int? = null,
)
