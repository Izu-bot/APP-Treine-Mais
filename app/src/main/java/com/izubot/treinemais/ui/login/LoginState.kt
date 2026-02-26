package com.izubot.treinemais.ui.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val loginFailure: Boolean = false,
    val isPasswordVisible: Boolean = false
)
