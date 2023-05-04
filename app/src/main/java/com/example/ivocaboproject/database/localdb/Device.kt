package com.example.ivocaboproject.database.localdb

import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "registerdate")
    val registerdate: String,
    @ColumnInfo(name = "macaddress")
    val macaddress: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "parsedeviceid")
    val objectId: String,
)

@Dao
interface deviceDao {
    @Query("SELECT COUNT(*) FROM devices")
    fun count(): Flow<Int>

    @Query("SELECT * FROM devices")
    fun list(): Flow<List<Device>>

    @Query("SELECT * FROM devices WHERE macaddress=:macaddress LIMIT 1")
    fun findbyMacAddress(macaddress: String): Flow<Device>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg device: Device)

    @Update
    suspend fun update(vararg device: Device)

    @Delete
    suspend fun delete(device: Device)
}
