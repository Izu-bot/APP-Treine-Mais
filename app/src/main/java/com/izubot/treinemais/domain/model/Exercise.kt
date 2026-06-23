package com.izubot.treinemais.domain.model

data class Exercise (
    val id: String,
    val name: String,
    val sets: String? = null,
    val reps: String? = null,
    val description: String = ""
)