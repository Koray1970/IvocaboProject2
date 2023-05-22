package com.example.ivocaboproject.bluetooth

import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
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
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ivocaboproject.R
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
    fun scanStart()
    fun scanStop()
    fun startReceiving()
    fun closeConnection()
    class BluetoothClientException(message: String) : Exception()
}

class IBluetoothClientRepository @Inject constructor() : IBluetoothClient {
    override fun getDeviceTrack(): Flow<BluetoothClientItemState> =
        getDeviceTrack()

    override fun scanStart() {
        TODO("Not yet implemented")
    }

    override fun scanStop() {
        TODO("Not yet implemented")
    }

    override fun startReceiving() {
        TODO("Not yet implemented")
    }

    override fun closeConnection() {
        TODO("Not yet implemented")
    }
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
    var bluetoothAdapter: BluetoothAdapter,
    val macaddress: String,
) : IBluetoothClient {
    private val TAG = BluetoothClient::class.java.simpleName
    private var bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private lateinit var request: ScanSettings
    private lateinit var filters: MutableList<ScanFilter>
    private lateinit var bluetoothGatt: BluetoothGatt
    private lateinit var gatt: BluetoothGatt
    private lateinit var leScanCallback: ScanCallback
    private lateinit var gattCallback: BluetoothGattCallback
    private var isScanning = false
    private var currentConnectionAttempt=0
    private val MAXIMUM_CONNECTION_ATTEMPTS=5

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getDeviceTrack(): Flow<BluetoothClientItemState> {
        return callbackFlow {
            /*if (!context.hasBluetoothPermission()) {
                throw IBluetoothClient.BluetoothClientException("Missing Bluetooth Permission")
            }*/
            //Scan Request Settig
            request = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .setLegacy(false)
                .setReportDelay(1000)
                .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                .build()
            //Scan Filters
            filters = mutableListOf<ScanFilter>()
            filters.add(ScanFilter.Builder().setDeviceAddress(macaddress).build())

            //GATT Callback
            gattCallback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(
                    gatt: BluetoothGatt,
                    status: Int,
                    newState: Int
                ) {
                    super.onConnectionStateChange(gatt, status, newState)
                    Log.v(TAG, "Read Remote RSSI : ${gatt?.readRemoteRssi()}")
                    Log.v(TAG, "Status : $status")
                    Log.v(TAG, "newState : $newState")
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            Log.v(TAG,"STATE_CONNECTED")
                            scanStop()
                            gatt.discoverServices()
                            gatt.readRemoteRssi()
                            gatt.readPhy()
                            this@BluetoothClient.gatt = gatt
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            currentConnectionAttempt=0
                            Log.v(TAG,"STATE_DISCONNECTED")
                            gatt.close()
                            startReceiving()
                        }
                    } else {
                        Log.v(TAG,"GATT_FAILED")
                        gatt.close()
                        currentConnectionAttempt+=1
                        if(currentConnectionAttempt<=MAXIMUM_CONNECTION_ATTEMPTS){
                            Log.v(TAG,"reconnect ble")
                            scanStart()
                        }
                        else{
                            Log.v(TAG,"Could not connect to ble device")
                            launch {
                                //error message (-100) :> Could not connect to ble device
                                send(BluetoothClientItemState(true, null, "-100"))
                            }
                        }
                    }
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


            //Scan Callback
            leScanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    super.onScanResult(callbackType, result)
                    launch {
                        send(BluetoothClientItemState(true, result.rssi, null))
                    }
                    if (isScanning) {
                        result.device.connectGatt(context, false, gattCallback)
                        isScanning = false
                        bluetoothLeScanner.stopScan(this)
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)
                    launch {
                        Log.v(TAG, "Error Code : $errorCode")
                        send(BluetoothClientItemState(true, null, errorCode.toString()))
                    }
                }
            }
            scanStart()
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

    @SuppressLint("MissingPermission")
    override fun scanStart() {
        isScanning = true
        //if(bluetoothAdapter==null) {
           /* val bluetoothManager: BluetoothManager =
                context.getSystemService<BluetoothManager>(BluetoothManager::class.java)
            bluetoothAdapter = bluetoothManager.getAdapter()*/
            //bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        //}

        bluetoothLeScanner.startScan(filters, request, leScanCallback)
    }

    @SuppressLint("MissingPermission")
    override fun scanStop() {
        isScanning = false
        bluetoothLeScanner.stopScan(leScanCallback)
        gatt.close()
    }

    @SuppressLint("MissingPermission")
    override fun startReceiving() {
        gatt.connect()
    }

    @SuppressLint("MissingPermission")
    override fun closeConnection() {
        gatt.close()
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

            //notificationManager.notify(1, updatedNotification.build())


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