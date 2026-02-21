package com.izubot.treinemais.domain.usecase

class ValidatePasswordConfirmationUseCase {
    operator fun invoke(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.isNotBlank()
    }
}