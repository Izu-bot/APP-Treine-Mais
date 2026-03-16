package com.izubot.treinemais.domain.abstraction

import androidx.annotation.StringRes

sealed interface ValidationResult {
    object Success : ValidationResult
    data class Error(
        @param:StringRes val message: Int
    ) : ValidationResult
}
