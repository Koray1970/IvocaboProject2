package com.example.ivocaboproject.bluetooth

import android.annotation.SuppressLint
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
import androidx.compose.runtime.MutableState
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters

class BleScanWorker(ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {
    private val TAG=BleScanWorker::class.java.simpleName
    private lateinit var macaddress:String
    private val bluetoothManager: BluetoothManager =
        ctx.getSystemService<BluetoothManager>(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter by lazy { bluetoothManager.adapter }
    val bluetoothLeScanner by lazy{ bluetoothAdapter.bluetoothLeScanner}
    override suspend fun doWork(): Result {
        macaddress= inputData.getString("macaddress").toString()
        Log.v(TAG,"Mac Address : $macaddress")

        return Result.success()
    }
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bleScanInit(){
        val request:ScanSettings=ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setLegacy(false)
            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
            .build()
        val filter= mutableListOf<ScanFilter>(ScanFilter.Builder().setDeviceAddress(macaddress).build())
        bluetoothLeScanner.startScan(filter,request,bleScanCallback)
    }
    private val bleScanCallback:ScanCallback=object:ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.v(TAG,"rssi : ${result?.rssi}")
        }
    }

    /*override suspend fun getForegroundInfo(): ForegroundInfo {
        return super.getForegroundInfo("100",createNotification())
    }
    private fun createNotification(){

    }*/
}