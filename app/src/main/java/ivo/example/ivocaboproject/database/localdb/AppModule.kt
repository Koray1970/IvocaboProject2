package ivo.example.ivocaboproject.database.localdb

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideApplicationDB(@ApplicationContext context: Context)=Room.databaseBuilder(context,AppDatabase::class.java,"ivocabo.db").allowMainThreadQueries().fallbackToDestructiveMigration().build()
    @Provides
    fun provideUserDao(appDatabase: AppDatabase)=appDatabase.userDao()
    @Provides
    fun provideUserReposity(userDao: userDao):UserRepository=UserOfflineRepository(userDao = userDao)

    @Provides
    fun provideDeviceDao(appDatabase: AppDatabase)=appDatabase.deviceDao()
    @Provides
    fun provideDeviceReposity(deviceDao: deviceDao):DeviceRepository=DeviceOfflineRepository(deviceDao = deviceDao)
}