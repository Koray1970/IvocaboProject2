package com.example.ivocaboproject.database.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class,Device::class,TrackArchive::class, MissingDevice::class,MissingArchive::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao():userDao
    abstract fun deviceDao():deviceDao
    abstract fun trackArchiveDao():trackArchiveDao
    abstract fun missingDeviceDao():missingDeviceDao
    abstract fun missingArvhiceDao():missingArchiveDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "ivocabo.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}