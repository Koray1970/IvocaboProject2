package com.example.ivocaboproject.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IvocaboFetcher(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private val TAG=IvocaboFetcher::class.java.simpleName
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val CONT=0
    private val NUMBER_OF_LOOP=5
    override suspend fun doWork(): Result {
        scanAndUpdateData()
        serviceScope.launch {
            delay(8000L)
            while (CONT<NUMBER_OF_LOOP) {
                stopScan()
                delay(8000L)
                scanAndUpdateData()
                delay(8000L)
            }
        }
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun scanAndUpdateData() {
        val bluetoothManager =
            applicationContext.getSystemService<BluetoothManager>(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        val filter = mutableListOf(ScanFilter.Builder().setDeviceName("MBeacon") .build())
        val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).setReportDelay(3000L).build()
        bluetoothLeScanner.startScan(filter, scanSettings, scanCallback)
    }
    @SuppressLint("MissingPermission")
    private fun stopScan(){
        bluetoothLeScanner.stopScan(scanCallback)
    }
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            var listofaddress=""
            results?.forEach {
                listofaddress+="${it.device.address}, " }
            Log.v(TAG,listofaddress)
        }
    }
}