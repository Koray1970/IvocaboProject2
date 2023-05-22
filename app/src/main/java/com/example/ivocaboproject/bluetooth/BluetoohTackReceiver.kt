package com.example.ivocaboproject.bluetooth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson

abstract class BluetoohTackReceiver : BroadcastReceiver() {
    private val gson: Gson = Gson()
    lateinit var scanResult: BluetoothTrackResponseItems
    private val SCAN_RESULT_INTENT_NAME = "ScanningResult"
    private val SCAN_RESULT_INTENT_EXTRA = "data"
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(SCAN_RESULT_INTENT_EXTRA))
            scanResult = gson.fromJson(
                intent.getStringExtra(SCAN_RESULT_INTENT_EXTRA),
                BluetoothTrackResponseItems::class.java
            )
    }
    abstract fun stopTrack(data:Boolean?)
}