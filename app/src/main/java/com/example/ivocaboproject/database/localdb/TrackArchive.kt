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

@Entity(tableName = "TrackArchive")
data class TrackArchive(
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
    @ColumnInfo(name ="parseuserid")
    val userobjectId:String,
    @ColumnInfo(name ="parseid")
    val objectId:String
)
@Dao
interface trackArchiveDao{
    @Query("SELECT COUNT(*) FROM trackarchive WHERE macaddress=:macaddress")
    fun count(macaddress: String): Flow<Int>

    @Query("SELECT * FROM trackarchive WHERE macaddress=:macaddress LIMIT 1")
    fun findbyMacAddress(macaddress: String):Flow<List<TrackArchive>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg trackArchive: TrackArchive)

    @Update
    suspend fun update(vararg trackArchive: TrackArchive)

    @Delete
    suspend fun delete(trackArchive: TrackArchive)
}
