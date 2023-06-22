package ivo.example.ivocaboproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.parse.Parse
import dagger.hilt.android.HiltAndroidApp
import ivo.example.ivocaboproject.database.localdb.UserViewModel

@HiltAndroidApp
class ivocaboprojectApplication : Application() {
    private val TAG = ivocaboprojectApplication::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        val viewModel=ViewModelProvider(applicationContext).get(UserViewModel::class.java)
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //location track notification
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            //Bluetooth track notification

            val soundUri =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.alarm)
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
            val bluetoothnotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            userViewModel.getUserNotification()
            userViewModel.userNotification.observeForever {
                if (it != null) {
                    if (it) {
                        bluetoothnotificationManager.createNotificationChannel(bluetoothchannel)
                        Log.v(TAG, "bluetoothnotificationManager.createNotificationChannel")
                    } else {
                        if (bluetoothnotificationManager.notificationChannels.contains(bluetoothchannel)) {
                            bluetoothnotificationManager.deleteNotificationChannel("ivocabobluetooth")
                            Log.v(TAG, "bluetoothnotificationManager.deleteNotificationChannel1")
                        }
                    }
                } else {
                    if (bluetoothnotificationManager.notificationChannels.contains(bluetoothchannel)) {
                        bluetoothnotificationManager.deleteNotificationChannel("ivocabobluetooth")
                        Log.v(TAG, "bluetoothnotificationManager.deleteNotificationChannel2")
                    }
                }
            }
        }
    }
}