package com.izubot.treinemais.ui.login

import androidx.annotation.StringRes

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val loginFailure: Boolean = false,
    val loginError: Int? = null,
    val isPasswordVisible: Boolean = false,
    val emailError: Boolean = false,
    @param:StringRes val errorPasswordMessage: Int? = null,
    @param:StringRes val errorEmailMessage: Int? = null,
    val passwordError: Boolean = false,
)
