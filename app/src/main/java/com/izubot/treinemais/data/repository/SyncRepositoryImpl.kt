package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.local.dao.UserDao
import com.izubot.treinemais.data.local.entities.SyncStatus
import com.izubot.treinemais.data.mappers.toEntity
import com.izubot.treinemais.data.mappers.toRemoteDto
import com.izubot.treinemais.data.remote.api.SyncApi
import com.izubot.treinemais.data.remote.dto.SyncPayloadDto
import com.izubot.treinemais.domain.repository.SyncRepository
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val syncApi: SyncApi
) : SyncRepository  {
    override suspend fun syncAll(): Result<Unit> {
        return try {
            val pendingUser = userDao.getUserWithSyncStatus(SyncStatus.PENDING)

            pendingUser?.let { userEntity ->
                    val payload = SyncPayloadDto(user = userEntity.toRemoteDto()
                )

                val response = syncApi.sync(payload)

                userDao.updateSyncStatus(userEntity.id, SyncStatus.SYNCED)

                val userFromServer = response.remoteUser.toEntity(userEntity.id)

                userDao.insertOrUpdateUser(userFromServer)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}