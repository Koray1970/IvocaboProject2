package ivo.example.ivocaboproject.bluetooth

import android.app.NotificationManager
import android.bluetooth.le.ScanResult
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import ivo.example.ivocaboproject.AppHelpers
import ivo.example.ivocaboproject.CurrentLoc
import ivo.example.ivocaboproject.R
import ivo.example.ivocaboproject.database.EventResultFlags
import ivo.example.ivocaboproject.database.ParseEvents
import ivo.example.ivocaboproject.database.localdb.AppDatabase
import ivo.example.ivocaboproject.database.localdb.Device
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackingEventManagement {
    private val appHelpers = AppHelpers()
    private val gson=Gson()
    private lateinit var getLoc: CurrentLoc
    private lateinit var soundUri: Uri
    private var notificationManager: NotificationManager? = null
    private lateinit var cDevice: Device
    private var parseEvents = ParseEvents()
    fun onInit(results: MutableList<ScanResult>?, context: Context) {


        val dbDeviceDao = AppDatabase.getDatabase(context).deviceDao()


        MainScope().launch {
            getLoc = CurrentLoc(context)
            getLoc.startScanLoc()
            delay(300L)
            var loc = getLoc.loc.value
            val trackDeviceList = dbDeviceDao.trackDeviceRowList()
            val deviceList = dbDeviceDao.listRow()
            if (trackDeviceList.isNotEmpty()) {
                soundUri =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.alarm)
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                trackDeviceList.forEach { f ->
                    val dMacAdd = appHelpers.formatedMacAddress(f.macaddress)
                    if (results?.isNotEmpty() == true) {
                        if (results.none { g -> g.device.address == dMacAdd }) {
                            var dbEventResult = parseEvents.AddTrackDeviceArchiveSecond(
                                context,
                                dbDeviceDao,
                                f,
                                loc?.latitude.toString(),
                                loc?.longitude.toString()
                            )
                            if (dbEventResult.eventResultFlags == EventResultFlags.SUCCESS) {
                                notificationOnInit(context, f)
                            }
                        }
                    } else {
                        var dbEventResult = parseEvents.AddTrackDeviceArchiveSecond(
                            context,
                            dbDeviceDao,
                            f,
                            loc?.latitude.toString(),
                            loc?.longitude.toString()
                        )
                        if (dbEventResult.eventResultFlags == EventResultFlags.SUCCESS) {
                            notificationOnInit(context, f)
                        }
                    }
                }

                BleTrackerService.MACADDRESS_LIST.postValue(dbDeviceDao.trackDeviceRowList())
            }
        }
    }

    fun notificationOnInit(context: Context, device: Device) {
        if (notificationManager!!.areNotificationsEnabled()) {
            //val bigText = context.getString(R.string.lostdevicenotification_msg)
            val notifyContent =context.getString(R.string.devicefarfrom)


            Intent().also { intent ->
                intent.action = "hasTrackNotification"
                intent.putExtra("detail", notifyContent)
                intent.putExtra("lostdevice", gson.toJson(device))
                context.sendBroadcast(intent)
            }



            val notification =
                NotificationCompat.Builder(
                    context,
                    "ivocabobluetooth"
                )
                    //.setContentTitle(context.getString(R.string.notificationtitle))
                    .setTicker(context.getString(R.string.notificationtitle))
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .setBigContentTitle(context.getString(R.string.notificationtitle))
                            .setSummaryText(notifyContent)
                            .bigText("${device.name} \n$notifyContent")
                    )
                    .setSmallIcon(R.drawable.outofrange_24)
                    .setOngoing(true)
                    .setSound(soundUri)
                    .setAutoCancel(true)
                    .build()

            notificationManager?.notify(
                (0..1000000).shuffled().last(),
                notification
            )


        }
    }
}