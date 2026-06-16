package com.izubot.treinemais.domain.model

data class Training(
    val id: String,
    val title: String,
    val exercises: List<Exercise>
)

data class Exercise(
    val id: String,
    val name: String,
    val sets: Int? = null,
    val reps: String? = null,
    val description: String = ""
)
