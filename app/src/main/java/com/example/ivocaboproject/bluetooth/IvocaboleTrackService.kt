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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IvocaboleTrackService : Service() {
    private val TAG=IvocaboleTrackService::class.java.simpleName
    private lateinit var scanFilter: MutableList<ScanFilter>
    private lateinit var scanSettings: ScanSettings
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            delay(3000L)
            scanFilter =
                mutableListOf<ScanFilter>(ScanFilter.Builder().setDeviceAddress(macaddress).build())
            scanSettings =
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
            bluetoothLeScanner.startScan(scanFilter, scanSettings, scanCallback)
            Log.v(TAG,"scanStart 1")
            while(true){
                delay(60000L)
                stopScan()
                delay(300000L)
                bluetoothLeScanner.startScan(scanFilter, scanSettings, scanCallback)
                Log.v(TAG,"scanStart 2")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        bluetoothLeScanner.stopScan(scanCallback)
        Log.v(TAG,"scanStop")
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            CURRENT_RSSI.postValue(result?.rssi)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            ERROR_CODE.postValue(errorCode)
        }
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
        var macaddress: String? = null
        var SCANNING_STATUS: Boolean = false
        var CURRENT_RSSI = MutableLiveData<Int>()
        var ERROR_CODE = MutableLiveData<Int>()
    }
}