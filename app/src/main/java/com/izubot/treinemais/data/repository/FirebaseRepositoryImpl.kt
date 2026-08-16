package com.izubot.treinemais.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.izubot.treinemais.domain.model.Feedback
import com.izubot.treinemais.domain.repository.FirebaseRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebase: FirebaseFirestore
): FirebaseRepository {
    override suspend fun submitFeedback(feedback: Feedback): Result<Unit> {
        return try {
            firebase.collection("feedback")
                .add(feedback)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}