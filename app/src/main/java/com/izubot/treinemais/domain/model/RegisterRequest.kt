package com.izubot.treinemais.domain.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val gender: String,
    val goals: String
)