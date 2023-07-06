package ivo.example.ivocaboproject.bluetooth

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import ivo.example.ivocaboproject.AppHelpers
import ivo.example.ivocaboproject.CurrentLoc
import ivo.example.ivocaboproject.R
import ivo.example.ivocaboproject.database.localdb.Device
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

interface IBleTRackerService {
    fun startScan()
    fun stopScan()
}

class UnTrackDeviceItems {
    var lostCounter: Int = 0
    var macAddress: String = ""
    var loc: LatLng = LatLng(0.0, 0.0)
}

class BleTrackerService : Service(), IBleTRackerService {
    private val TAG = BleTrackerService::class.java.simpleName
    private val gson = Gson()
    private val appHelpers = AppHelpers()

    private var scanFilter = mutableListOf<ScanFilter>()
    private lateinit var scanSettings: ScanSettings
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var unTrackItems = mutableListOf<UnTrackDeviceItems>()
    private var notificationManager: NotificationManager? = null
    private lateinit var soundUri: Uri
    private val getLoc: CurrentLoc = CurrentLoc(applicationContext)
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //appAlarm = MediaPlayer.create(applicationContext, R.raw.alarm)
        soundUri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.alarm)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @RequiresPermission(
        anyOf = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_SCAN
        )
    )
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(TAG, "here we are")
        //Log.v(TAG,gson.toJson(MACADDRESS_LIST.value))
        MACADDRESS_LIST.observeForever {
            if (it.isEmpty()) {
                stopScan()
            } else {
                stopScan()

                scanFilter = mutableListOf<ScanFilter>()
                it.forEach {
                    scanFilter.add(
                        ScanFilter.Builder()
                            .setDeviceAddress(appHelpers.formatedMacAddress(it.macaddress)).build()
                    )
                }
                startScan()
            }
            Log.v(TAG, "Scanfilter List : ${gson.toJson(scanFilter)}")

        }
        //if(scanFilter.isNotEmpty())


        return super.onStartCommand(intent, flags, startId)
    }

    /*@RequiresPermission(
        anyOf = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_SCAN
        )
    )*/
    @SuppressLint("MissingPermission")
    override fun startScan() {
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter.isEnabled) {
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            if (scanFilter.isNotEmpty()) {
                scanSettings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                    .setReportDelay(10000L)
                    .build()
                bluetoothLeScanner?.startScan(scanFilter, scanSettings, scanCallBack)
                Log.v(TAG, "startScan")
            }
        }
    }

    /* @RequiresPermission(
         anyOf = arrayOf(
             android.Manifest.permission.ACCESS_FINE_LOCATION,
             android.Manifest.permission.BLUETOOTH_SCAN
         )
     )*/
    @SuppressLint("MissingPermission")
    override fun stopScan() {
        bluetoothLeScanner?.stopScan(scanCallBack)
    }

    val scanCallBack = object : ScanCallback() {
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            if (results?.isNotEmpty() == true) {
                //scan result is not empty
                MACADDRESS_LIST.value?.forEach {
                    if (results.none { rr -> rr.device.address == it.macaddress }) {
                        unTrackDeviceItemOnListEvent(it)
                    }
                }
            } else {
                //scan result is empty
                MACADDRESS_LIST.value?.forEach {
                    unTrackDeviceItemOnListEvent(it)
                }
            }
            var lostDevices = unTrackItems.filter { ff -> ff.lostCounter >= 20 }
            if (lostDevices.isNotEmpty()) {

                val bigText =getString(R.string.lostdevicenotification_msg)
                if (notificationManager!!.areNotificationsEnabled()) {

                    //val notifyContent = currentDevice.macaddress + " - " + getString(R.string.devicefarfrom)

                    val notification =
                        NotificationCompat.Builder(
                            applicationContext,
                            "ivocabobluetooth"
                        )
                            .setContentTitle(getString(R.string.notificationtitle))
                            .setTicker(getString(R.string.notificationtitle))
                            //.setContentText(notifyContent)
                            .setStyle(
                                NotificationCompat.BigTextStyle()
                                    .bigText(bigText)
                            )
                            .setSmallIcon(R.drawable.outofrange_24)
                            .setOngoing(true)
                            .setSound(soundUri)
                            .build()
                }






                Intent().also { intent ->
                    intent.action = "hasTrackNotification"
                    intent.putExtra("detail", bigText)
                    intent.putExtra("lostdevicelist", gson.toJson(lostDevices))
                    sendBroadcast(intent)
                }
            }
            //Log.v(TAG, "Lost Devices : ${gson.toJson(lostDevices)}")
        }
    }

    fun unTrackDeviceItemOnListEvent(device: Device) {
        getLoc.startScanLoc()
        var loc = getLoc.loc.value
        var unTrackDeviceItems = UnTrackDeviceItems()
        if (unTrackItems.isNotEmpty()) {
            if (unTrackItems.any { i -> i.macAddress == device.macaddress }) {
                unTrackItems.find { uti -> uti.macAddress == device.macaddress }!!.lostCounter++
            } else {
                unTrackDeviceItems.macAddress = device.macaddress
                unTrackDeviceItems.lostCounter = 1
                if (loc != null)
                    unTrackDeviceItems.loc = LatLng(loc.latitude, loc.longitude)
                unTrackItems.add(unTrackDeviceItems)
            }
        } else {
            unTrackDeviceItems.macAddress = device.macaddress
            unTrackDeviceItems.lostCounter = 1
            unTrackItems.add(unTrackDeviceItems)
        }
    }

    companion object {
        var MACADDRESS_LIST = MutableLiveData<List<Device>>()
    }
}