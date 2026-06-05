package com.izubot.treinemais.data.repository

import android.util.Log
import androidx.room.Transaction
import com.izubot.treinemais.data.local.dao.UserDao
import com.izubot.treinemais.data.local.entities.SyncStatus
import com.izubot.treinemais.data.local.entities.User
import com.izubot.treinemais.data.mappers.toRemoteDto
import com.izubot.treinemais.data.remote.api.SyncApi
import com.izubot.treinemais.data.remote.dto.SyncPayloadDto
import com.izubot.treinemais.domain.repository.SyncRepository
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val syncApi: SyncApi
) : SyncRepository  {

    @Transaction
    override suspend fun syncAll(): Result<Unit> {
        var pendingUser: User? = null
        return try {
            pendingUser = userDao.getUserWithSyncStatus(SyncStatus.PENDING)

            if (pendingUser != null) {
                userDao.updateSyncStatus(pendingUser.id, SyncStatus.IN_PROGRESS)

                val payload = SyncPayloadDto(pendingUser.toRemoteDto())
                val response = syncApi.sync(payload)

                if (response.success)
                {
                    userDao.updateSyncStatus(pendingUser.id, SyncStatus.SYNCED)
                    Log.i("SyncWorker", "User ${pendingUser.guidUser} synced successfully")
                } else {
                    userDao.updateSyncStatus(pendingUser.id, SyncStatus.ERROR)
                    return Result.failure(Exception("Server returned success=false"))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            pendingUser?.let {
                userDao.updateSyncStatus(it.id, SyncStatus.ERROR)
            }
            Log.e("SyncWorker", "Sync failed", e)
            Result.failure(e)
        }
    }
}