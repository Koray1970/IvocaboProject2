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
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import ivo.example.ivocaboproject.AppHelpers
import ivo.example.ivocaboproject.CurrentLoc
import ivo.example.ivocaboproject.R
import ivo.example.ivocaboproject.database.localdb.Device
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class DisconnectedDevice(var macaddress: String, var countofdisconnect: Int)
class IvocaboleTrackService : Service() {
    private val TAG = IvocaboleTrackService::class.java.simpleName
    private val appHelpers = AppHelpers()
    private val gson = Gson()
    private var scanFilter = mutableListOf<ScanFilter>()
    private lateinit var scanSettings: ScanSettings
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var disconnectedControl = ArrayList<DisconnectedDevice>()
    private var appAlarm: MediaPlayer = MediaPlayer()
    private var notificationManager: NotificationManager? = null
    private var deviceSize = MutableLiveData<Int>(0)
    private var deviceList = arrayListOf<String>()
    private lateinit var soundUri: Uri
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        devicelist.observeForever {
            deviceSize.postValue(it.size)
            if (it.isEmpty()) {
                deviceSize.postValue(0)
                stopScan()
            } else {
                if (scanFilter == null)
                    scanFilter = mutableListOf<ScanFilter>()
                it.forEach {

                    var nMacaddress = appHelpers.formatedMacAddress(it.macaddress)
                    if (BluetoothAdapter.checkBluetoothAddress(nMacaddress)) {
                        deviceList.add(nMacaddress)
                        val checkHasDevice =
                            scanFilter.filter { f -> f.deviceAddress == nMacaddress }
                        if (checkHasDevice.isEmpty())
                            scanFilter.add(
                                ScanFilter.Builder().setDeviceAddress(nMacaddress).build()
                            )
                    }
                }
            }
        }
        when (SCANNING_STATUS) {
            false -> stopScan()
            true -> startScan()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    private fun startScan() {
        bluetoothManager = getSystemService<BluetoothManager>(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        GlobalScope.launch {
            delay(2000L)
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            if (scanFilter.isNotEmpty()) {
                scanSettings =
                    ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                        .setReportDelay(10000L).build()
                bluetoothLeScanner?.startScan(scanFilter, scanSettings, scanCallback)
                SCANNING_STATUS = true
                Log.v(TAG, "scanStart 1")


                while (true) {
                    delay(120000L) //2 minute
                    stopScan()
                    delay(15000L) //15 second
                    bluetoothLeScanner?.startScan(scanFilter, scanSettings, scanCallback)
                    SCANNING_STATUS = true
                    Log.v(TAG, "scanStart 2")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        SCANNING_STATUS = false
        bluetoothLeScanner?.stopScan(scanCallback)
        Log.v(TAG, "scanStop")
    }


    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            CURRENT_RSSI.postValue(Math.abs(result!!.rssi))
            /*if (disconnectedControl > 0) {
                disconnectedControl = 0
                DISCONNECTED.postValue(null)
            }*/
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            var maclist = arrayListOf<String>()
            results?.forEach { maclist.add(it.device.address) }
            var findResult = deviceList.filter { maclist.contains(it) }
            if (disconnectedControl.isNotEmpty()) {
                disconnectedControl.removeIf { findResult.contains(it.macaddress) }
            }
            var fResult = deviceList.filterNot { g -> maclist.contains(g) }
            if (fResult!!.isNotEmpty()) {
                fResult.forEach { d ->
                    if (disconnectedControl.isEmpty()) {
                        disconnectedControl.add(DisconnectedDevice(d, 1))
                    } else {
                        var disconnectFilters =
                            disconnectedControl.filter { it.macaddress == d }
                        if (disconnectFilters.isNotEmpty()) {
                            var currentDevice = disconnectFilters.first()

                            currentDevice.countofdisconnect = currentDevice.countofdisconnect + 1
                            if (currentDevice.countofdisconnect >= 2) {
                                currentDevice.countofdisconnect = 0
                                var notifyContent =
                                    currentDevice.macaddress + " - " + getString(R.string.devicefarfrom)
                                var latLng = LatLng(0.0, 0.0)
                                val getLoc = CurrentLoc(applicationContext)
                                MainScope().launch {
                                    getLoc.startScanLoc()
                                    getLoc.loc.observeForever {
                                        if (it != null) {
                                            latLng = it
                                            val notification =
                                                NotificationCompat.Builder(
                                                    applicationContext,
                                                    "ivocabobluetooth"
                                                )
                                                    .setContentTitle(getString(R.string.notificationtitle))
                                                    .setTicker(getString(R.string.notificationtitle))
                                                    .setContentText(notifyContent)
                                                    .setStyle(NotificationCompat.BigTextStyle()
                                                        .bigText("Device current lost location  :\n ${it.latitude} , ${it.longitude}"))
                                                    .setSmallIcon(R.drawable.outofrange_24)
                                                    .setOngoing(true)
                                                    .setSound(soundUri)
                                                    .build()

                                            notificationManager?.notify(
                                                fResult.indexOf(d),
                                                notification
                                            )
                                        }
                                    }
                                }


                            }
                        } else
                            disconnectedControl.add(DisconnectedDevice(d, 1))
                    }
                }
                if (disconnectedControl.isNotEmpty())
                    Log.v(TAG, gson.toJson(disconnectedControl))
            } else {
                if (findResult.isNotEmpty()) {
                    if (disconnectedControl.isEmpty()) {
                        disconnectedControl.removeIf { findResult.contains(it.macaddress) }
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            DISCONNECTED.postValue(true)
            ERROR_CODE.postValue(errorCode)
        }
    }


    private fun startNotification() {
        val notification = NotificationCompat.Builder(applicationContext, "ivocabobluetooth")
            .setContentTitle(getString(R.string.notificationtitle))
            .setTicker(getString(R.string.notificationtitle))
            .setContentText(getString(R.string.devicefarfrom))
            .setSmallIcon(R.drawable.outofrange_24)
            .setOngoing(true)
            .build()
        startForeground(1, notification)
    }


    override fun onDestroy() {
        super.onDestroy()
        stopScan()
        stopSelf()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    companion object {
        var devicelist = MutableLiveData<List<Device>>()
        var macaddress: String? = null
        var SCANNING_STATUS: Boolean = false
        var CURRENT_RSSI = MutableLiveData<Int>()
        var DISCONNECTED = MutableLiveData<Boolean?>()
        var ERROR_CODE = MutableLiveData<Int>()
        var SHOW_NOTIFICATION = MutableLiveData<Boolean?>()
    }
}