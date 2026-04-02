package com.izubot.treinemais.data.local.dto

import com.izubot.treinemais.data.local.entities.SyncStatus
import com.izubot.treinemais.data.local.entities.User
import kotlinx.serialization.Serializable

@Serializable
data class UserLocalDto(
    val guidUser: String,
    val name: String,
    val email: String,
    val gender: String,
    val birthDate: String,
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val isDirty: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
fun UserLocalDto.toDomain() = User(
    guidUser = guidUser,
    fullName = name,
    email = email,
    gender = gender,
    birthDate = birthDate,
    lastUpdatedAt = lastUpdatedAt,
    isDirty = isDirty,
    syncStatus = syncStatus
)