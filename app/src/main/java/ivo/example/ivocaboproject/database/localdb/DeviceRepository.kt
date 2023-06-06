package ivo.example.ivocaboproject.database.localdb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun count(): Int
    fun list():List<Device>
    fun trackDeviceList():List<Device>
    fun findbyMacAddress(macaddress: String): Device
    suspend fun insert(device: Device)
    suspend fun update(device: Device)
    suspend fun delete(device: Device)
}
class DeviceOfflineRepository(private val deviceDao: deviceDao) : DeviceRepository {
    override fun count(): Int = deviceDao.count()
    override fun list(): List<Device> =deviceDao.list()
    override fun trackDeviceList(): List<Device> = deviceDao.trackDeviceList()
    override fun findbyMacAddress(macaddress: String): Device =deviceDao.findbyMacAddress(macaddress)
    override suspend fun insert(device: Device) = deviceDao.insert(device)
    override suspend fun update(device: Device) = deviceDao.update(device)
    override suspend fun delete(device: Device) = deviceDao.delete(device)
}