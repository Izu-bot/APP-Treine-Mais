package com.izubot.treinemais.domain.usecase

import android.util.Patterns
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.abstraction.ValidationResult
import javax.inject.Inject

class ValidateNameUseCase @Inject constructor() {
    operator fun invoke(name: String) : ValidationResult {
       return when {
           name.isBlank() -> ValidationResult.Error(R.string.register_validate_name)
           name.length < 3 -> ValidationResult.Error(R.string.register_invalid_name)
           else -> ValidationResult.Success
       }
    }
}