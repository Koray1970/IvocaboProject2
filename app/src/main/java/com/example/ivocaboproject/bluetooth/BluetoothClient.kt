package com.example.ivocaboproject.bluetooth

import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ivocaboproject.R
import com.example.ivocaboproject.hasBluetoothPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class BluetoothClientItemState(
    val isloading: Boolean = true,
    val rssi: Int? = null,
    val errorMassage: String? = null,
) : Parcelable

interface IBluetoothClient {
    fun getDeviceTrack(): Flow<BluetoothClientItemState>

    class BluetoothClientException(message: String) : Exception()
}

class IBluetoothClientRepository @Inject constructor() : IBluetoothClient {
    override fun getDeviceTrack(): Flow<BluetoothClientItemState> =
        getDeviceTrack()
}

@HiltViewModel
class IBluetoothClientViewModel @Inject constructor(
    private val repo: IBluetoothClientRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val uiState = MutableStateFlow(BluetoothClientItemState(false, null, null))
    fun consumableState() = uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1000)
            uiState.value
        }
    }

    fun handleViewEvent(viewEvent: IBluetoothClientViewEvent) {
        val currentState = uiState.value
        val items = currentState.apply { viewEvent }
        uiState.value = uiState.value.copy(true, items.rssi, items.errorMassage)
    }
}

sealed class IBluetoothClientViewEvent {
    data class AddItem(val itemState: BluetoothClientItemState) : IBluetoothClientViewEvent()
}


class BluetoothClient(
    private val context: Context,
    val bluetoothAdapter: BluetoothAdapter,
    val macaddress: String,
) : IBluetoothClient {
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val TAG = BluetoothClient::class.java.simpleName
    private lateinit var bluetoothGatt: BluetoothGatt

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getDeviceTrack(): Flow<BluetoothClientItemState> {
        return callbackFlow {
            /*if (!context.hasBluetoothPermission()) {
                throw IBluetoothClient.BluetoothClientException("Missing Bluetooth Permission")
            }*/
            var device = bluetoothAdapter.getRemoteDevice(macaddress)
            //device.createBond()

            val bluetoothGattCallback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(
                    gatt: BluetoothGatt?,
                    status: Int,
                    newState: Int
                ) {
                    super.onConnectionStateChange(gatt, status, newState)
                    Log.v(TAG, "Read Remote RSSI : ${gatt?.readRemoteRssi()}")
                    Log.v(TAG, "Status : $status")
                    Log.v(TAG, "newState : $newState")
                    /*when (newState) {
                        133,
                        BluetoothProfile.STATE_DISCONNECTING,
                        BluetoothProfile.STATE_DISCONNECTED -> gatt?.connect()

                        *//*BluetoothProfile.STATE_CONNECTED,
                        BluetoothProfile.STATE_CONNECTING -> {
                            gatt?.discoverServices()
                            gatt?.readPhy()
                            gatt?.readRemoteRssi()
                        }*//*
                    }*/
                    /*when(status){
                        BluetoothGatt.GATT_FAILURE->gatt?.connect()
                        BluetoothGatt.GATT_SUCCESS->{
                            gatt?.discoverServices()
                            gatt?.readPhy()
                            gatt?.readRemoteRssi()}
                    }*/
                }

                override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
                    super.onReadRemoteRssi(gatt, rssi, status)
                    Log.v(TAG, "RSSI : $rssi")
                    Log.v(TAG, "onReadRemoteRssi status : $status")
                    launch {
                        send(BluetoothClientItemState(true, rssi, null))
                    }
                }
            }



            bluetoothGatt = device.connectGatt(context, true, bluetoothGattCallback)
            bluetoothGatt.readRemoteRssi()
            bluetoothGatt.readPhy()

            /*bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            val request = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                *//*.setLegacy(true)
                .setMatchMode(ScanSettings.MATCH_MODE_STICKY)*//*
                .build()
            val filters = mutableListOf<ScanFilter>()
            filters.add(ScanFilter.Builder().setDeviceAddress(macaddress).build())



            val leScanCallback: ScanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    super.onScanResult(callbackType, result)
                    launch {
                        send(BluetoothClientItemState(true, result.rssi, null))
                    }
                }

                override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                    super.onBatchScanResults(results)
                    Log.v("BluetoothClient", results?.last()?.rssi.toString())
                    launch {
                        send(BluetoothClientItemState(true, results?.last()?.rssi, null))

                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)
                    launch {
                        Log.v(TAG,"Error Code : $errorCode")
                        send(BluetoothClientItemState(true, null, errorCode.toString()))
                    }
                }
            }


            bluetoothLeScanner.startScan(filters, request, leScanCallback)*/

            awaitClose {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    if (context.checkPermission(
                            BLUETOOTH_SCAN,
                            100,
                            100
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                        bluetoothGatt.close()
                    //bluetoothLeScanner.stopScan(leScanCallback)
                } else {
                    bluetoothGatt.close()
                    //bluetoothLeScanner.stopScan(leScanCallback)
                }
            }
        }
    }
}

class BluetoothClientService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var macaddress: String
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var iBluetoothClient: IBluetoothClient
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.getAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.hasExtra("macaddress") == true) {
            macaddress = intent?.getStringExtra("macaddress").toString()
            if (bluetoothAdapter != null) {
                iBluetoothClient =
                    BluetoothClient(applicationContext, bluetoothAdapter!!, macaddress)
            } else
                stop()
        }
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun start() {

        val notification = NotificationCompat.Builder(this, "ivocabobluetooth")
            .setContentTitle("Tracking Ivocabo...")
            .setContentText("RSSI: null")
            .setSmallIcon(R.drawable.baseline_bluetooth_searching_24)
            .setOngoing(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        iBluetoothClient.getDeviceTrack().catch { e -> e.printStackTrace() }.onEach {
            val updatedNotification = notification.setContentText(
                "RSSI: ${it.rssi}"
            )
            Intent("bluetoothscanresult").apply {
                putExtra("ivocabosearchresult", it)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(this)
            }

            notificationManager.notify(1, updatedNotification.build())


        }.launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

}