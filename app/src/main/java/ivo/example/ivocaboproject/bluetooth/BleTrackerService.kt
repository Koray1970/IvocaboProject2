package ivo.example.ivocaboproject.bluetooth

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
import android.content.SharedPreferences
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import ivo.example.ivocaboproject.AppHelpers
import ivo.example.ivocaboproject.database.localdb.Device
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

interface IBleTRackerService {
    fun startScan()
    fun stopScan()
}

class UnTrackDeviceItems {
    var lostCounter: Int = 0
    var macAddress: String = ""
}

class BleTrackerService : Service(), IBleTRackerService {
    private val TAG = BleTrackerService::class.java.simpleName
    private val gson = Gson()
    private val appHelpers = AppHelpers()

    private var scanFilter = mutableListOf<ScanFilter>()
    private lateinit var scanSettings: ScanSettings
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var unTrackItems = mutableListOf<UnTrackDeviceItems>()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresPermission(
        anyOf = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_SCAN
        )
    )
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(TAG, "here we are")
        //Log.v(TAG,gson.toJson(MACADDRESS_LIST.value))
        MACADDRESS_LIST.observeForever {
            if (it.isEmpty()) {
                stopScan()
            } else {
                stopScan()

                scanFilter = mutableListOf<ScanFilter>()
                it.forEach {
                    scanFilter.add(
                        ScanFilter.Builder()
                            .setDeviceAddress(appHelpers.formatedMacAddress(it.macaddress)).build()
                    )
                }
                startScan()
            }
            Log.v(TAG, "Scanfilter List : ${gson.toJson(scanFilter)}")

        }
        //if(scanFilter.isNotEmpty())


        return super.onStartCommand(intent, flags, startId)
    }

    /*@RequiresPermission(
        anyOf = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_SCAN
        )
    )*/
    @SuppressLint("MissingPermission")
    override fun startScan() {
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter.isEnabled) {
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            if (scanFilter.isNotEmpty()) {
                scanSettings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                    .setReportDelay(10000L)
                    .build()
                bluetoothLeScanner?.startScan(scanFilter, scanSettings, scanCallBack)
                Log.v(TAG,"startScan")
            }
        }
    }

   /* @RequiresPermission(
        anyOf = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_SCAN
        )
    )*/
   @SuppressLint("MissingPermission")
    override fun stopScan() {
        bluetoothLeScanner?.stopScan(scanCallBack)
    }

    val scanCallBack = object : ScanCallback() {
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            if (results?.isNotEmpty() == true) {
                //scan result is not empty
                MACADDRESS_LIST.value?.forEach {
                    if (results.none { rr -> rr.device.address == it.macaddress }) {
                        unTrackDeviceItemOnListEvent(it)
                    }
                }
            } else {
                //scan result is empty
                MACADDRESS_LIST.value?.forEach {
                    unTrackDeviceItemOnListEvent(it)
                }
            }
            var lostDevices = unTrackItems.filter { ff -> ff.lostCounter >= 4 }
            Log.v(TAG, "Lost Devices : ${gson.toJson(lostDevices)}")
        }
    }

    fun unTrackDeviceItemOnListEvent(device: Device) {
        var unTrackDeviceItems = UnTrackDeviceItems()
        if (unTrackItems.isNotEmpty()) {
            if (unTrackItems.any { i->i.macAddress==device.macaddress }) {
                unTrackItems.find { uti -> uti.macAddress == device.macaddress }!!.lostCounter++
            }
            else {
                unTrackDeviceItems.macAddress = device.macaddress
                unTrackDeviceItems.lostCounter = 1
                unTrackItems.add(unTrackDeviceItems)
            }
        } else {
            unTrackDeviceItems.macAddress = device.macaddress
            unTrackDeviceItems.lostCounter = 1
            unTrackItems.add(unTrackDeviceItems)
        }
    }

    companion object {
        var MACADDRESS_LIST = MutableLiveData<List<Device>>()
    }
}