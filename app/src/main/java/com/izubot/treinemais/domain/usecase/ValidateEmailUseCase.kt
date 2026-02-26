package com.izubot.treinemais.domain.usecase

import android.util.Patterns
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.model.ValidationResult
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String) : ValidationResult {
        if (email.isBlank()) return ValidationResult.Error(R.string.register_validate_email)

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return ValidationResult.Error(R.string.register_invalid_email)

        return ValidationResult.Success
    }
}