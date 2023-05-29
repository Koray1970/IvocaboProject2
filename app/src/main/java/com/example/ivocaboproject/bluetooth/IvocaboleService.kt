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
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IvocaboleService : Service() {
    private val TAG = IvocaboleService::class.java.simpleName
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private lateinit var scanSettings: ScanSettings
    private lateinit var scanFilter: MutableList<ScanFilter>
    private var macAddress: String? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (START_SCAN) {
            true -> {
                if (intent?.hasExtra("macaddress") == true) {
                    macAddress = intent.getStringExtra("macaddress")
                    bluetoothManager =
                        getSystemService<BluetoothManager>(BluetoothManager::class.java)
                    bluetoothAdapter = bluetoothManager.adapter
                    scanSettings =
                        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .build()
                    scanFilter =
                        mutableListOf(ScanFilter.Builder().setDeviceAddress(macAddress).build())
                    GlobalScope.launch {
                        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
                        bluetoothLeScanner?.startScan(scanFilter, scanSettings, scanCallback)
                        Log.v(TAG, "$TAG is started!!!")
                    }
                }
            }

            false -> {

                if (bluetoothLeScanner != null)
                    bluetoothLeScanner?.stopScan(scanCallback)
                Log.v(TAG, "$TAG is stopped!!!")
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.v(TAG, "SS RSSI:${result?.rssi}")
            CURRENT_RSSI.postValue(Math.abs(result!!.rssi).toInt())
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            ErrorCode.postValue(errorCode)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        if (bluetoothLeScanner != null)
            bluetoothLeScanner?.stopScan(scanCallback)

        stopSelf()
        super.onDestroy()
    }

    companion object {
        var CURRENT_RSSI = MutableLiveData<Int>()
        var ErrorCode = MutableLiveData<Int>()
        var START_SCAN = false
    }
}