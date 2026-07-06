package com.izubot.treinemais.domain.model

data class Training(
    val id: String,
    val title: String,
    val exercises: List<Exercise>
)