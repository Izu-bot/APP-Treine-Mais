package com.izubot.treinemais.utils

// Fórmula Epley
fun Calculate1RM(weight: Double, reps: Int): Double {
    return weight * (1.0 + reps / 30.0)
}