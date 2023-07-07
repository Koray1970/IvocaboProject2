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
import androidx.compose.runtime.MutableState
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
import kotlinx.parcelize.Parcelize

interface IBleTRackerService {
    fun startScan()
    fun stopScan()
}


class BleTrackerService : Service(), IBleTRackerService {
    private val TAG = BleTrackerService::class.java.simpleName
    private val appHelpers = AppHelpers()
    private val gson = Gson()
    private var scanFilter = mutableListOf<ScanFilter>()
    private lateinit var scanSettings: ScanSettings
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var callbackCounter = 0
    private lateinit var trackingEventManagement: TrackingEventManagement
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        trackingEventManagement = TrackingEventManagement()
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter.isEnabled)
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

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
                callbackCounter = 0
                stopScan()
            } else {
                scanFilter = mutableListOf<ScanFilter>()
                it.forEach {
                    scanFilter.add(
                        ScanFilter.Builder()
                            .setDeviceAddress(appHelpers.formatedMacAddress(it.macaddress))
                            .build()
                    )
                }
                callbackCounter = 0
                startScan()
            }
        }
        MainScope().launch {
            delay(220)
            startScan()
        }

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
        Log.v(TAG, "Scanfilter List : ${gson.toJson(scanFilter)}")
        BleTrackerService.IS_SEVICE_RUNNING.postValue(true)
        if (bluetoothAdapter.isEnabled) {
            if (scanFilter.isNotEmpty()) {
                scanSettings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                    .setReportDelay(3000L)
                    .build()
                bluetoothLeScanner?.startScan(scanFilter, scanSettings, scanCallBack)
            }
            //Log.v(TAG, "startScan")
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
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.v(TAG, "onScanResult :  ${gson.toJson(result)}")
        }

        @SuppressLint("MissingPermission")
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            callbackCounter++
            results?.sortBy({ it.timestampNanos })
            var newlist = results?.distinctBy { it.device.address }?.toMutableList()
            Log.v(TAG, "Scan Results Raw :  ${gson.toJson(newlist)}")
            //Log.v(TAG, "Scan Results :  ${gson.toJson(newlist)}")

            //Log.v(TAG, "Scan Results2 :  ${gson.toJson(results)}")

            if (callbackCounter >= 5) {
                trackingEventManagement.onInit(newlist, applicationContext)
                callbackCounter = 0
            }
            Log.v(TAG, "Counter :  $callbackCounter")
        }
    }

    companion object {
        var MACADDRESS_LIST = MutableLiveData<List<Device>>()
        var IS_SEVICE_RUNNING = MutableLiveData<Boolean>(false)
        var STOP_ALARM=MutableLiveData<Boolean>()
    }
}