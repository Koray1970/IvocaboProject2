package com.example.ivocaboproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.parse.Parse
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ivocaboprojectApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //location track notification
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            //Bluetooth track notification
            val bluetoothchannel = NotificationChannel(
                "ivocabobluetooth",
                "IvocaboBluetooth",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val bluetoothnotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            bluetoothnotificationManager.createNotificationChannel(bluetoothchannel)
        }
    }
}