package com.example.ivocaboproject.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data class BluetoothTrackResponseItems(
    var isScanning: Boolean?,
    var hasError: Boolean = false,
    var errorCode: String?,
    var rssi: Int?,
    var txtpower: Int?,
) : Parcelable


class BluetoothTrackService : Service() {
    private val TAG = BluetoothTrackService::class.java.simpleName
    private val gson: Gson = Gson()
    private val RESULT_INTENT_ACTION_NAME = "SCANNING_RESULT"
    private val RESULT_INTENT_NAME = "data"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var macaddress: String
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var scanSettings: ScanSettings =
        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
    private lateinit var scanFilter: MutableList<ScanFilter>
    private var isLooping = true

    companion object {
        const val SERVICE_START = "SERVICE_START"
        const val SERVICE_STOP = "SERVICE_STOP"
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.hasExtra("macaddress") == true) {
            macaddress = intent.getStringExtra("macaddress").toString()
            scanFilter =
                mutableListOf<ScanFilter>(ScanFilter.Builder().setDeviceAddress(macaddress).build())
        } else {
            Intent(RESULT_INTENT_ACTION_NAME).apply {
                putExtra(
                    RESULT_INTENT_NAME,
                    gson.toJson(
                        BluetoothTrackResponseItems(false, true, "-220", null, null)
                    )
                )
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(this)
            }
        }
        when (intent?.action) {
            SERVICE_START -> {
                isLooping = true
                eventHolder()
            }

            SERVICE_STOP -> stopServising()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun eventHolder() {
        startScan()
        serviceScope.launch {
            delay(30000L)
            while (isLooping==true) {

                stopScan()
                delay(8000L)
                startScan()
                delay(30000L)
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun startScan() {
        bluetoothManager = getSystemService<BluetoothManager>(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter.isEnabled) {
            if (scanFilter.size > 0) {
                bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

                bluetoothLeScanner.startScan(scanFilter, scanSettings, scanCallback)

            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        bluetoothLeScanner.stopScan(scanCallback)
        Log.v(TAG, "Service stopping2...")
    }

    private fun stopServising() {
        stopScan()
        stopSelf()
        Log.v(TAG, "Service stopping1...")
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("NewApi")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                Log.v(TAG, "RSSI : ${result.rssi}")

                Intent(RESULT_INTENT_ACTION_NAME).apply {
                    putExtra(
                        RESULT_INTENT_NAME,
                        gson.toJson(
                            BluetoothTrackResponseItems(
                                true,
                                false,
                                null,
                                result.rssi,
                                result.txPower
                            )
                        )
                    )
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(this)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.v(TAG, "errorCode : $errorCode")

            Intent(RESULT_INTENT_ACTION_NAME).apply {
                putExtra(
                    RESULT_INTENT_NAME,
                    gson.toJson(
                        BluetoothTrackResponseItems(
                            true,
                            true,
                            errorCode.toString(),
                            null,
                            null
                        )
                    )
                )
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        isLooping = false
        serviceScope.launch {
            bluetoothLeScanner.stopScan(scanCallback)
        }
        Log.v(TAG, "Service destroyed...")
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }
}
