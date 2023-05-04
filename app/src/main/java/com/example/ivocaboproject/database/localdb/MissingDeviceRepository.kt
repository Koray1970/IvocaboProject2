package com.example.ivocaboproject.database.localdb

import kotlinx.coroutines.flow.Flow

interface MissingDeviceRepository {
    fun count(macaddress: String): Flow<Int>
    fun findbyMacAddress(macaddress: String):Flow<List<MissingDevice>>
    suspend fun insert(missingDevice: MissingDevice)
    suspend fun update(missingDevice: MissingDevice)
    suspend fun delete(missingDevice: MissingDevice)
}
class MissingDeviceOfflineRepository(private val missingDeviceDao: missingDeviceDao) : MissingDeviceRepository {
    override fun count(macaddress: String): Flow<Int> = missingDeviceDao.count(macaddress)
    override fun findbyMacAddress(macaddress: String): Flow<List<MissingDevice>> = missingDeviceDao.findbyMacAddress(macaddress)
    override suspend fun insert(missingDevice: MissingDevice) = missingDeviceDao.insert(missingDevice)
    override suspend fun update(missingDevice: MissingDevice) = missingDeviceDao.update(missingDevice)
    override suspend fun delete(missingDevice: MissingDevice) = missingDeviceDao.delete(missingDevice)
}