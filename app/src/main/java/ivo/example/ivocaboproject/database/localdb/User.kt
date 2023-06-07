package ivo.example.ivocaboproject.database.localdb

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
import java.sql.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo(name="registerdate")
    val registerdate: Date,
    @ColumnInfo(name ="username")
    val username:String,
    @ColumnInfo(name ="email")
    val email:String,
    @ColumnInfo(name ="password")
    val password:String,
    @ColumnInfo(name ="parseid")
    var objectId:String?
)
@Dao
interface userDao{
    @Query("SELECT COUNT(id) FROM users")
    fun count():Int
    @Query("SELECT * FROM users LIMIT 1")
    fun findUser(): User
    @Query("SELECT * FROM users WHERE email=:email LIMIT 1")
    fun findbyEmail(email: String): User

    @Query("SELECT * FROM users WHERE email=:email AND password=:password LIMIT 1")
    fun findbyEmailPass(email: String,password: String): User

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg users: User)

    @Update
    suspend fun update(vararg users: User)

    @Delete
    suspend fun delete(user: User)
}
