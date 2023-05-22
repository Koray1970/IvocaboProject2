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
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.parcelize.Parcelize

@Parcelize
data class BluetoothTrackResponseItems(
    var isScanning: Boolean?,
    var hasError: Boolean = false,
    var errorCode: String?,
    var rssi: Int?,
    var txtpower: Int?
) : Parcelable


class BluetoothTrackService : Service() {
    private val TAG = BluetoothTrackService::class.java.simpleName
    private val gson: Gson = Gson()
    private val RESULT_INTENT_NAME = "ScanningResult"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var macaddress: String
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var scanSettings: ScanSettings =
        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
    private lateinit var scanFilter: MutableList<ScanFilter>

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        val bluetoothManager = getSystemService<BluetoothManager>(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.hasExtra("macaddress") == true) {
            macaddress = intent.getStringExtra("macaddress").toString()
            scanFilter =
                mutableListOf<ScanFilter>(ScanFilter.Builder().setDeviceAddress(macaddress).build())
        } else {
            val onStartIntent = Intent(RESULT_INTENT_NAME)
            onStartIntent.putExtra(
                "data",
                gson.toJson(
                    BluetoothTrackResponseItems(false, true, "-220", null, null)
                )
            )
            sendBroadcast(onStartIntent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private val serviceReceiver = object : BluetoohTackReceiver() {
        override fun stopTrack(data: Boolean?) {
            if (data != null && data == true) {
                stopScan()
                stopSelf()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun startScan() {
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
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("NewApi")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                val resultIntent = Intent(RESULT_INTENT_NAME)
                resultIntent.putExtra(
                    "data",
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
                sendBroadcast(resultIntent)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            val errorIntent = Intent(RESULT_INTENT_NAME)
            errorIntent.putExtra(
                "data",
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
            sendBroadcast(errorIntent)
        }
    }

    override fun onDestroy() {
        stopScan()
        stopSelf()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }
}
