package com.example.ivocaboproject.database.localdb

import android.os.Parcelable
import androidx.lifecycle.LiveData
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
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "registerdate")
    val registerdate: Date,
    @ColumnInfo(name = "macaddress")
    val macaddress: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "latitude")
    val latitude: String,
    @ColumnInfo(name = "longitude")
    val longitude: String,
    @ColumnInfo(name = "parsedeviceid")
    var objectId: String,
): Parcelable

@Dao
interface deviceDao {
    @Query("SELECT COUNT(*) FROM devices")
    fun count(): Int

    @Query("SELECT * FROM devices")
    fun list():List<Device>

    @Query("SELECT * FROM devices WHERE macaddress=:macaddress LIMIT 1")
    fun findbyMacAddress(macaddress: String): Device

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg device: Device)

    @Update
    suspend fun update(vararg device: Device)

    @Delete
    suspend fun delete(device: Device)
}
