package com.izubot.treinemais.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.data.local.datasource.DataStorePrefs
import com.izubot.treinemais.domain.abstraction.ValidationResult
import com.izubot.treinemais.domain.usecase.ConfirmEmailUseCase
import com.izubot.treinemais.domain.usecase.LoginUseCase
import com.izubot.treinemais.domain.usecase.ValidateEmailUseCase
import com.izubot.treinemais.domain.usecase.ValidatePasswordConfirmationUseCase
import com.izubot.treinemais.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val confirmEmailUseCase: ConfirmEmailUseCase,
    private val loginUseCase: LoginUseCase,
    private val validatePasswordConfirmationUseCase: ValidatePasswordConfirmationUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val dataStorePrefs: DataStorePrefs
) : ViewModel() {

    private val _channel = Channel<UiEvent>()
    val channel = _channel.receiveAsFlow()

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

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            loginUseCase(_state.value)
                .onSuccess { token ->
                    dataStorePrefs.saveTokens(token.accessToken, token.refreshToken)
                    onSuccess()
                }
                .onFailure { error ->
                    _channel.send(UiEvent.Toast(context.getString(R.string.login_error_message)))
                    Log.d("Login", "Login error: ${error.message}")
                }

            _state.update { it.copy(isLoading = false) }
        }
    }

    fun confirmEmail(token: String) {
        viewModelScope.launch {
            confirmEmailUseCase(token)
                .onSuccess {
                    _channel.send(UiEvent.Toast(context.getString(R.string.login_email_verificate)))
                }
                .onFailure { error ->
                    Log.d("Login", "$error")
                    _channel.send(UiEvent.Toast(context.getString(R.string.login_error_email)))
                }
        }
    }
}
