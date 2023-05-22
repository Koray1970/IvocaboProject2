package com.example.ivocaboproject.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface IBluetoothleConnect {
    fun startScan()
    fun stopScan()
}

class BluetoothleConnect(
    private val ctx: Context,
    val macaddress: String,
    notificationEnabled: Boolean?
) : IBluetoothleConnect {
    private val TAG = BluetoothleConnect::class.java.simpleName
    private val bluetoothManager =
        ctx.getSystemService<BluetoothManager>(BluetoothManager::class.java)
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothGatt: BluetoothGatt
    private val scanSettings: ScanSettings
    private val scanFilter: MutableList<ScanFilter>

    init {
        scanSettings =
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
        scanFilter =
            mutableListOf<ScanFilter>(
                ScanFilter.Builder().setDeviceAddress(macaddress).build()
            )
    }

    @SuppressLint("MissingPermission")
    override fun startScan() {
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter.isEnabled) {
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            bluetoothLeScanner.startScan(scanFilter, scanSettings, bluetoothLeScanCallback)
        }
    }

    private val bluetoothLeScanCallback = object : ScanCallback() {
        @SuppressLint("NewApi", "MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                Log.v(TAG, "Device RSSI : ${result?.rssi}")
                //if(result.isConnectable){
                bluetoothDevice = result.device
                //bluetoothGatt = bluetoothDevice.connectGatt(ctx, false, bluetoothGattCallback)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }
    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            //Log.v(TAG,"newState : $newState")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                if (ctx.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
            /*gatt?.discoverServices()
            gatt?.readRemoteRssi()*/
            //Log.v(TAG, "readRemoteRssi : ${gatt?.readRemoteRssi()}")
            when (newState) {
                BluetoothProfile.STATE_CONNECTING -> {
                    gatt?.readRemoteRssi()
                }

                BluetoothProfile.STATE_CONNECTED -> {

                    bluetoothGatt = gatt!!

                    //stopScan()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    //stopScan()
                    gatt?.connect()
                    //Log.v(TAG, "Gatt Reconnect")
                }
            }
        }


        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            if (ctx.checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            gatt?.readRemoteRssi()
            Log.v(TAG, "status : $status")
            Log.v(TAG, "Gatt RSSI : $rssi")
        }

    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        bluetoothLeScanner.stopScan(bluetoothLeScanCallback)
        Log.v(TAG, "Scan Stop")
    }
}