package com.izubot.treinemais.domain.model

import androidx.annotation.StringRes

sealed interface ValidationResult {
    object Success : ValidationResult
    data class Error(
        @param:StringRes val message: Int
    ) : ValidationResult
}
