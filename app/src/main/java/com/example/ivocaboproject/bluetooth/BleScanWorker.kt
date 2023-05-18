package com.example.ivocaboproject.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class BleScanWorker(ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {
    private val TAG=BleScanWorker::class.java.simpleName
    private lateinit var macaddress:String
    private val bluetoothManager: BluetoothManager =
        ctx.getSystemService<BluetoothManager>(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter by lazy { bluetoothManager.adapter }
    override suspend fun doWork(): Result {
        macaddress= inputData.getString("macaddress").toString()



        return Result.success()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bleScanInit(){
        val request:ScanSettings=ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setLegacy(false)
            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
            .build()
        val filter:ScanFilter=ScanFilter.Builder().setDeviceAddress(macaddress).build()

    }
    private val bleScanCallback:ScanCallback=object:ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.v(TAG,"rssi : ${result?.rssi}")
        }
    }
}