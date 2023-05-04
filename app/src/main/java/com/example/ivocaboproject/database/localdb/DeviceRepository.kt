package com.example.ivocaboproject.database.localdb

import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun count(): Flow<Int>
    fun list():Flow<List<Device>>
    fun findbyMacAddress(macaddress: String): Flow<Device>
    suspend fun insert(device: Device)
    suspend fun update(device: Device)
    suspend fun delete(device: Device)
}
class DeviceOfflineRepository(private val deviceDao: deviceDao) : DeviceRepository {
    override fun count(): Flow<Int> = deviceDao.count()
    override fun list(): Flow<List<Device>> =deviceDao.list()
    override fun findbyMacAddress(macaddress: String): Flow<Device> =deviceDao.findbyMacAddress(macaddress)
    override suspend fun insert(device: Device) = deviceDao.insert(device)
    override suspend fun update(device: Device) = deviceDao.update(device)
    override suspend fun delete(device: Device) = deviceDao.delete(device)
}