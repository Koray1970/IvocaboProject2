package com.example.ivocaboproject.database.localdb

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

@Entity(tableName = "missingdevices")
data class MissingDevice(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo(name="registerdate")
    val registerdate:String,
    @ColumnInfo(name ="macaddress")
    val macaddress:String,
    @ColumnInfo(name ="latitude")
    val latitude:String,
    @ColumnInfo(name ="longitude")
    val longitude:String,
    @ColumnInfo(name ="deviceobjectid")
    val deviceObjectId:String,
    @ColumnInfo(name ="parseid")
    val objectId:String
)
@Dao
interface missingDeviceDao{
    @Query("SELECT COUNT(*) FROM missingdevices WHERE macaddress=:macaddress")
    fun count(macaddress: String): Flow<Int>

    @Query("SELECT * FROM missingdevices WHERE macaddress=:macaddress LIMIT 1")
    fun findbyMacAddress(macaddress: String):Flow<List<MissingDevice>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg missingDevice: MissingDevice)

    @Update
    suspend fun update(vararg missingDevice: MissingDevice)

    @Delete
    suspend fun delete(missingDevice: MissingDevice)
}
