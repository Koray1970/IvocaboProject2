package ivo.example.ivocaboproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
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

            val soundUri= Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.alarm)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val bluetoothchannel = NotificationChannel(
                "ivocabobluetooth",
                "IvocaboBluetooth",
                NotificationManager.IMPORTANCE_DEFAULT,

            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bluetoothchannel.setSound(soundUri, audioAttributes)
            }
            val bluetoothnotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            bluetoothnotificationManager.createNotificationChannel(bluetoothchannel)
        }
    }
}