package ivo.example.ivocaboproject.bluetooth

import android.app.Service
import android.bluetooth.le.ScanFilter
import android.companion.AssociationRequest
import android.companion.BluetoothLeDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData

class CopanianDeviceService : Service() {
    private var deviceManager: CompanionDeviceManager? = null
    private lateinit var deviceFilter: BluetoothLeDeviceFilter
    private var scanFilter = mutableListOf<ScanFilter>()
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        deviceManager = getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun StartDiscover() {
        if (Build.VERSION.SDK_INT <= 32) {
            deviceFilter = BluetoothLeDeviceFilter.Builder()
                .setScanFilter(ScanFilter.Builder().setDeviceAddress(MACADDRESS).build()).build()
            val pairingRequest: AssociationRequest = AssociationRequest.Builder()
                .addDeviceFilter(deviceFilter)
                .setSingleDevice(true)
                .build()
            deviceManager?.associate(pairingRequest, object : CompanionDeviceManager.Callback() {
                override fun onAssociationPending(intentSender: IntentSender) {
                    super.onAssociationPending(intentSender)

                }

                override fun onFailure(p0: CharSequence?) {
                    TODO("Not yet implemented")
                }
            }, null)
        }
    }

    companion object {
        lateinit var MACADDRESS: String
    }
}