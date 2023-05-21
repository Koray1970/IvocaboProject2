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
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface IBluetoothleConnect {
    suspend fun startScan()
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

    @SuppressLint("MissingPermission")
    override suspend fun startScan() {
        bluetoothScanner()
        //delay(8000)
        //startScan()
    }

    @SuppressLint("MissingPermission")
    private fun bluetoothScanner() {
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter.isEnabled) {
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            MainScope().launch {

                val scanSettings =
                    ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
                val scanFilter =
                    mutableListOf<ScanFilter>(
                        ScanFilter.Builder().setDeviceAddress(macaddress).build()
                    )
                bluetoothLeScanner.startScan(scanFilter, scanSettings, bluetoothLeScanCallback)

            }
        }
    }

    private val bluetoothLeScanCallback = object : ScanCallback() {
        @SuppressLint("NewApi", "MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                //Log.v(TAG, "Device RSSI : ${result?.rssi}")
                //if(result.isConnectable){
                bluetoothDevice = result.device

                GlobalScope.launch {


                    //delay(3000)
                    bluetoothGatt = bluetoothDevice.connectGatt(ctx, false, bluetoothGattCallback)
                    delay(3000)
                    stopScan()
                }
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
            gatt?.discoverServices()
            gatt?.readRemoteRssi()
            Log.v(TAG, "readRemoteRssi : ${gatt?.readRemoteRssi()}")
            when (newState) {
                BluetoothProfile.STATE_CONNECTING -> {
                    //stopScan()
                }

                BluetoothProfile.STATE_CONNECTED -> {

                    bluetoothGatt = gatt!!

                    //stopScan()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    //stopScan()
                    gatt?.connect()
                    Log.v(TAG, "Gatt Reconnect")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            if (status == BluetoothGatt.GATT_SUCCESS)
                gatt?.readRemoteRssi()
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            Log.v(TAG, "Gatt RSSI : $rssi")
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        bluetoothLeScanner.stopScan(bluetoothLeScanCallback)
        Log.v(TAG, "Scan Stop")
    }
}