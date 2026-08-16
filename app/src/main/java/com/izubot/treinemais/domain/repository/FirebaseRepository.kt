package com.izubot.treinemais.domain.repository

import com.izubot.treinemais.domain.model.Feedback

interface FirebaseRepository {
    suspend fun submitFeedback(feedback: Feedback): Result<Unit>
}