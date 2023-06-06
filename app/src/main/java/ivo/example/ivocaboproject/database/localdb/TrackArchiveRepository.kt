package ivo.example.ivocaboproject.database.localdb

import kotlinx.coroutines.flow.Flow

interface TrackArchiveRepository {
    fun count(macaddress: String): Flow<Int>
    fun findbyMacAddress(macaddress: String): Flow<List<TrackArchive>>
    suspend fun insert(trackArchive: TrackArchive)
    suspend fun update(trackArchive: TrackArchive)
    suspend fun delete(trackArchive: TrackArchive)
}

class TrackArchiveOfflineRepository(private val trackArchiveDao: trackArchiveDao) :
    TrackArchiveRepository {
    override fun count(macaddress: String): Flow<Int> = trackArchiveDao.count(macaddress)
    override fun findbyMacAddress(macaddress: String): Flow<List<TrackArchive>> =
        trackArchiveDao.findbyMacAddress(macaddress)

    override suspend fun insert(trackArchive: TrackArchive) = trackArchiveDao.insert(trackArchive)
    override suspend fun update(trackArchive: TrackArchive) = trackArchiveDao.update(trackArchive)
    override suspend fun delete(trackArchive: TrackArchive) = trackArchiveDao.delete(trackArchive)
}