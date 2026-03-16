package com.izubot.treinemais.domain.usecase

import com.izubot.treinemais.R
import com.izubot.treinemais.domain.abstraction.ValidationResult
import javax.inject.Inject

class ValidatePasswordConfirmationUseCase @Inject constructor() {
    operator fun invoke(password: String, confirmPassword: String): ValidationResult {
        return when {
            password != confirmPassword -> ValidationResult.Error(R.string.register_invalid_password_confirmation)
            password.length < 8 -> ValidationResult.Error(R.string.register_invalid_length_password)
            else -> ValidationResult.Success
        }
    }
}