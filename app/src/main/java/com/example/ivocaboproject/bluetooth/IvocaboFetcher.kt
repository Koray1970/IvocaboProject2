package com.example.ivocaboproject.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ivocaboproject.AppHelpers
import com.example.ivocaboproject.database.ParseEvents
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class IvocaboFetcher(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private val TAG = IvocaboFetcher::class.java.simpleName
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson: Gson = Gson()
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val CONT = 0
    private val NUMBER_OF_LOOP = 5
    private lateinit var latlong: LatLng
    val mutablelistofaddress = MutableStateFlow(arrayListOf<String>())
    private var listofmacaddress = arrayListOf<String>()
    private val appHelpers=AppHelpers()
    override suspend fun doWork(): Result {
        Log.i(TAG, "IvocaboFetcher Start")
        if(inputData.getString("latlong")!=null) {
            latlong = gson.fromJson(inputData.getString("latlong"), LatLng::class.java)
            if (latlong != null) {
                startScan()
                serviceScope.launch {
                    delay(6000L)
                    stopScan()
                    delay(12000L)
                    if(listofmacaddress.size>0) {
                        val tmplistdata = arrayListOf<String>()
                        listofmacaddress.forEach {
                            if (!tmplistdata.contains(it))
                                tmplistdata.add(it)
                        }
                        val parseevents = ParseEvents()
                        parseevents.CheckAndUpdateMissingDevice(tmplistdata, latlong)
                    }
                }
            }
        }
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun startScan() {
        val bluetoothManager =
            applicationContext.getSystemService<BluetoothManager>(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter

        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        //val filter = mutableListOf(ScanFilter.Builder().setDeviceName("MBeacon") .build())
        val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .setReportDelay(2000L).build()
        bluetoothLeScanner.startScan(null, scanSettings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        bluetoothLeScanner.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            if (results != null) {
                if (results.size > 0) {
                    results?.forEach {
                        listofmacaddress.add(appHelpers.unformatedMacAddress(it.device.address))
                    }
                }
            }
        }
    }
}