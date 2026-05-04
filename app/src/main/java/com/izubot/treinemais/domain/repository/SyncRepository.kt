package com.izubot.treinemais.domain.repository

interface SyncRepository {
    suspend fun syncAll(): Result<Unit>
}