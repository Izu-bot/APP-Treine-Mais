package com.izubot.treinemais.data.mappers

import com.izubot.treinemais.data.local.entities.SyncStatus
import com.izubot.treinemais.data.local.entities.User
import com.izubot.treinemais.data.remote.dto.UserApiDto


fun User.toRemoteDto(): UserApiDto = UserApiDto(
    userId = this.guidUser,
    name = this.fullName,
    email = this.email,
    gender = this.gender,
    birthDate = this.birthDate,
    updatedAt = this.lastUpdatedAt
)

// Converte o DTO da API de volta para a Entity do Room
fun UserApiDto.toEntity(localId: String): User = User(
    id = localId,
    guidUser = this.userId,
    fullName = this.name,
    email = this.email,
    gender = this.gender,
    birthDate = this.birthDate,
    lastUpdatedAt = this.updatedAt,
    syncStatus = SyncStatus.SYNCED,
    isDirty = false
)