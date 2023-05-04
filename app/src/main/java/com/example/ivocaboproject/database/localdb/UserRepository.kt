package com.example.ivocaboproject.database.localdb

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun count(): Flow<Int>
    fun findbyEmail(email: String): Flow<User>
    suspend fun insert(user: User)
    suspend fun update(user: User)
    suspend fun delete(user: User)
}

class UserOfflineRepository(private val userDao: userDao) : UserRepository {
    override fun count(): Flow<Int> = userDao.count()
    override fun findbyEmail(email: String): Flow<User> = userDao.findbyEmail(email)
    override suspend fun insert(user: User) = userDao.insert(user)
    override suspend fun update(user: User) = userDao.update(user)
    override suspend fun delete(user: User) = userDao.delete(user)
}