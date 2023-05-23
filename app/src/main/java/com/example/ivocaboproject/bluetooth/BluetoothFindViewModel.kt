package com.example.ivocaboproject.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

interface IBluetoothFind {
    fun startScan(macaddress: String): Flow<ScanResult>
    fun stopScan(): Unit

    class error(message: String)
}

class BluetoothFindRepository() : IBluetoothFind {
    override fun startScan(macaddress: String): Flow<ScanResult> {
        TODO("Not yet implemented")
    }

    override fun stopScan() {
        TODO("Not yet implemented")
    }
}

data class dbBluetoothData(var rssi: Int?, var distance: String)

@HiltViewModel
class BluetoothFindViewModel @Inject constructor(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val TAG = BluetoothFindViewModel::class.java.simpleName
    private val data = MutableStateFlow(dbBluetoothData(null, ""))
    val isScanning = MutableStateFlow(false)

    fun consumableState() = data.asStateFlow()

    private lateinit var bluetoothLeScanner: BluetoothLeScanner

    @SuppressLint("MissingPermission")
    suspend fun startScan(macAddress: String) {
        try {
            val bluetoothManager =
                application.getSystemService<BluetoothManager>(BluetoothManager::class.java)
            val adapter by lazy { bluetoothManager.adapter }
            if (adapter.isEnabled) {
                viewModelScope.launch {
                    delay(3000)
                    bluetoothLeScanner = adapter.bluetoothLeScanner
                    val filter =
                        mutableListOf<ScanFilter>(
                            ScanFilter.Builder().setDeviceAddress(macAddress).build()
                        )
                    val scanSettings: ScanSettings = ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build()
                    bluetoothLeScanner.startScan(filter, scanSettings, scanCallback)
                    isScanning.value = true
                    Log.v(TAG, " startScan()")
                }
            }
        } catch (exception: Exception) {
            Log.v(TAG, "exception : ${exception.message}")
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun stopScanning() {
        Log.v(TAG, "stopScanning()")
        bluetoothLeScanner.stopScan(scanCallback)
        isScanning.value = false
    }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("NewApi")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.v(TAG, "isScanning(1) :  ${isScanning.value}")
            Log.v(TAG, "LRSSI -> ${result?.rssi}")
            Log.v(TAG, "LTXP -> ${result?.txPower}")

            if (result != null) {
                viewModelScope.launch {
                    val srssi = if (result?.rssi != null) result.rssi.toDouble() else 0.0
                    var stxp = if (result?.txPower != null) result.txPower.toDouble() else 0.0
                    val realdist=10.0.pow(((-59) - (srssi)) / (10 * 2))
                    val dd = realdist.toString()
                    Log.v(TAG, "Distance -> $dd")
                    data.value = dbBluetoothData(result.rssi, dd)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.v(TAG, "Error Code : $errorCode")
            viewModelScope.launch {
                stopScanning()
                Log.v(TAG, "isScanning(2) : ${isScanning.value}")
            }
        }
    }
}