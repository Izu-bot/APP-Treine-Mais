package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.local.dao.UserDao
import com.izubot.treinemais.data.local.entities.SyncStatus
import com.izubot.treinemais.data.local.entities.User
import com.izubot.treinemais.domain.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override fun getUser() = userDao.getUser()
    override suspend fun insertOrUpdateUser(user: User) = userDao.insertOrUpdateUser(user)

    override suspend fun deleteUserById(id: String) = userDao.deleteUserById(id)

    override suspend fun saveProfileImage(uri: String) {
        val currentUser = userDao.getUser().firstOrNull()
        currentUser?.let { user ->
            val updatedUser = user.copy(localPhotoPath = uri)
            userDao.insertOrUpdateUser(updatedUser)
        }
    }

    override suspend fun getUnsyncedData(status: SyncStatus) = userDao.getUserWithSyncStatus(status)
}