package ivo.example.ivocaboproject.database.localdb

import kotlinx.coroutines.flow.Flow

interface MissingArchiveRepository {
    fun count(macaddress: String): Flow<Int>
    fun findbyMacAddress(macaddress: String): Flow<List<MissingArchive>>
    suspend fun insert(missingArchive: MissingArchive)
    suspend fun update(missingArchive: MissingArchive)
    suspend fun delete(missingArchive: MissingArchive)
}

class MissingArchiveOfflineRepository(private val missingArchiveDao: missingArchiveDao) :
    MissingArchiveRepository {
    override fun count(macaddress: String): Flow<Int> = missingArchiveDao.count(macaddress)
    override fun findbyMacAddress(macaddress: String): Flow<List<MissingArchive>> =
        missingArchiveDao.findbyMacAddress(macaddress)

    override suspend fun insert(missingArchive: MissingArchive) =
        missingArchiveDao.insert(missingArchive)

    override suspend fun update(missingArchive: MissingArchive) =
        missingArchiveDao.update(missingArchive)

    override suspend fun delete(missingArchive: MissingArchive) =
        missingArchiveDao.delete(missingArchive)
}