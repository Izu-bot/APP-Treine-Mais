package com.izubot.treinemais.data.repository

import com.izubot.treinemais.data.local.dao.UserDao
import com.izubot.treinemais.data.local.entities.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun getUser() = userDao.getUser()
    suspend fun insertOrUpdateUser(user: User) = userDao.insertOrUpdateUser(user)

    suspend fun deleteUserById(id: String) = userDao.deleteUserById(id)

    suspend fun saveProfileImage(uri: String) {
        val currentUser = userDao.getUser().firstOrNull()
        currentUser?.let { user ->
            val updatedUser = user.copy(localPhotoPath = uri)
            userDao.insertOrUpdateUser(updatedUser)
        }
    }
}