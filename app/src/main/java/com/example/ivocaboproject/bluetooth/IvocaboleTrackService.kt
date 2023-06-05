package com.example.ivocaboproject.bluetooth

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.work.ForegroundInfo
import com.example.ivocaboproject.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IvocaboleTrackService : Service() {
    private val TAG = IvocaboleTrackService::class.java.simpleName
    private lateinit var scanFilter: MutableList<ScanFilter>
    private lateinit var scanSettings: ScanSettings
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var disconnectedControl: Int = 0
    private var appAlarm: MediaPlayer= MediaPlayer()
    private var notificationManager:NotificationManager?=null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
        appAlarm= MediaPlayer.create(applicationContext, R.raw.alarm)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        SHOW_NOTIFICATION.observeForever {
            when(it){
                false->notificationManager?.cancel(1)
                else->{
                    startNotification()
                }
            }
        }

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
            delay(2000L)
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            //delay(3000L)
            scanFilter =
                mutableListOf<ScanFilter>(ScanFilter.Builder().setDeviceAddress(macaddress).build())
            scanSettings =
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(1000L).build()
            bluetoothLeScanner?.startScan(scanFilter, scanSettings, scanCallback)
            SCANNING_STATUS = true
            Log.v(TAG, "scanStart 1")
            startNotification()

            while (true) {
                delay(120000L) //2 minute
                stopScan()
                delay(60000L) //1minute
                bluetoothLeScanner?.startScan(scanFilter, scanSettings, scanCallback)
                SCANNING_STATUS = true
                Log.v(TAG, "scanStart 2")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        SCANNING_STATUS = false
        bluetoothLeScanner?.stopScan(scanCallback)
        Log.v(TAG, "scanStop")
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            CURRENT_RSSI.postValue(Math.abs(result!!.rssi))
            if (disconnectedControl > 0) {
                disconnectedControl = 0
                DISCONNECTED.postValue(null)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            val fResult = results?.filter { it.device.address == macaddress }
            Log.v(TAG,"SCANNING_STATUS : $SCANNING_STATUS")
            if (fResult!!.isEmpty()) {
                disconnectedControl++
                if (disconnectedControl!! >= 8) {
                    DISCONNECTED.postValue(true)
                    CURRENT_RSSI.postValue(0)
                    appAlarm?.start()
                    val notification = NotificationCompat.Builder(applicationContext,"ivocabobluetooth")
                        .setContentTitle(getString(R.string.notificationtitle))
                        .setTicker(getString(R.string.notificationtitle))
                        .setContentText(getString(R.string.devicefarfrom))
                        .setSmallIcon(R.drawable.outofrange_24)
                        .setOngoing(true)
                        .build()
                    notificationManager?.notify(1, notification)
                }
            } else {
                appAlarm?.pause()
                CURRENT_RSSI.postValue(fResult.first().rssi)
                disconnectedControl = 0
                DISCONNECTED.postValue(null)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            disconnectedControl++
            if (disconnectedControl!! >= 5)
                DISCONNECTED.postValue(true)
            ERROR_CODE.postValue(errorCode)
        }
    }


    private fun startNotification(){
        val notification = NotificationCompat.Builder(applicationContext,"ivocabobluetooth")
            .setContentTitle(getString(R.string.notificationtitle))
            .setTicker(getString(R.string.notificationtitle))
            .setContentText(getString(R.string.devicefarfrom))
            .setSmallIcon(R.drawable.outofrange_24)
            .setOngoing(true)
            .build()
        startForeground(1,notification)
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
        var DISCONNECTED = MutableLiveData<Boolean?>()
        var ERROR_CODE = MutableLiveData<Int>()
        var SHOW_NOTIFICATION=MutableLiveData<Boolean?>()
    }
}