package com.example.ivocaboproject.database.localdb

import android.content.Context

interface AppContainer {
    val userRepository: UserRepository
    val deviceRepository: DeviceRepository
    val trackArchiveRepository:TrackArchiveRepository
    val missingDeviceRepository:MissingDeviceRepository
    val missingArchiveRepository:MissingArchiveRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val userRepository: UserRepository by lazy {
        UserOfflineRepository(
            AppDatabase.getDatabase(
                context
            ).userDao()
        )
    }
    override val deviceRepository: DeviceRepository by lazy {
        DeviceOfflineRepository(
            AppDatabase.getDatabase(
                context
            ).deviceDao()
        )
    }
    override val trackArchiveRepository: TrackArchiveRepository by lazy {
        TrackArchiveOfflineRepository(
            AppDatabase.getDatabase(
                context
            ).trackArchiveDao()
        )
    }
    override val missingDeviceRepository: MissingDeviceRepository by lazy {
        MissingDeviceOfflineRepository(
            AppDatabase.getDatabase(
                context
            ).missingDeviceDao()
        )
    }
    override val missingArchiveRepository: MissingArchiveRepository by lazy {
        MissingArchiveOfflineRepository(
            AppDatabase.getDatabase(
                context
            ).missingArvhiceDao()
        )
    }
}