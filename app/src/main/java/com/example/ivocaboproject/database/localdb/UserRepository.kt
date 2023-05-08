package com.example.ivocaboproject.database.localdb

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import javax.inject.Inject

interface UserRepository {
    fun count():Int
    fun findUser(): User
    fun findbyEmail(email: String): User
    suspend fun insert(user: User)
    suspend fun update(user: User)
    suspend fun delete(user: User)
}

class UserOfflineRepository @Inject constructor(private val userDao: userDao) : UserRepository {
    override fun count(): Int = userDao.count()
    override fun findUser(): User =userDao.findUser()
    override fun findbyEmail(email: String): User = userDao.findbyEmail(email)
    override suspend fun insert(user: User) = userDao.insert(user)
    override suspend fun update(user: User) = userDao.update(user)
    override suspend fun delete(user: User) = userDao.delete(user)
}