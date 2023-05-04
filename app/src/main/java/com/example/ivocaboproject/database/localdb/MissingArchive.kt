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

@Entity(tableName = "missingarchive")
data class MissingArchive (
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
interface missingArchiveDao{
    @Query("SELECT COUNT(*) FROM missingarchive WHERE macaddress=:macaddress")
    fun count(macaddress: String): Flow<Int>

    @Query("SELECT * FROM missingarchive WHERE macaddress=:macaddress LIMIT 1")
    fun findbyMacAddress(macaddress: String):Flow<List<MissingArchive>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg missingArchive: MissingArchive)

    @Update
    suspend fun update(vararg missingArchive: MissingArchive)

    @Delete
    suspend fun delete(missingArchive: MissingArchive)
}