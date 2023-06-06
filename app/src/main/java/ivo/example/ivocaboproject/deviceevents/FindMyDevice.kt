package ivo.example.ivocaboproject.deviceevents

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface iFindMyDevice {
    fun startScan()
    fun stopScan()
}

class FindMyDevice(val ctx: Context, val macaddress: String, viewModel: FindMyDeviceViewModel) :
    iFindMyDevice {
    private val TAG = FindMyDevice::class.java.simpleName
    private lateinit var bluetoothManager: BluetoothManager

    //private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null

    @SuppressLint("MissingPermission")
    override fun startScan() {
        bluetoothManager = ctx.getSystemService<BluetoothManager>(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter
        MainScope().launch {
            delay(2000L)
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

            val scanSettings =
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
            val scanFilter =
                listOf<ScanFilter>(ScanFilter.Builder().setDeviceAddress(macaddress).build())
            bluetoothLeScanner?.startScan(scanFilter, scanSettings, scanCallback)
            Log.v(TAG, "Find Device Scan Started!!!")
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.v(TAG, "result rssi : ${result?.rssi}")
            viewModel.updateEventResult(dbDeviceEventResult(result?.rssi, null))
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.v(TAG, "errorcode : $errorCode")
            viewModel.updateEventResult(dbDeviceEventResult(null, errorCode))
        }
    }
}