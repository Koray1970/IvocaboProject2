package com.example.ivocaboproject.bluetooth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson

class BluetoohTackReceiver : BroadcastReceiver() {
    private val gson: Gson = Gson()
    var scanResult = BluetoothTrackResponseItems(false, false, null, null, null)
    private val SCAN_RESULT_INTENT_ACTION_NAME = "SCANNING_RESULT"
    private val SCAN_RESULT_INTENT_EXTRA = "data"
    override fun onReceive(context: Context?, intent: Intent?) {

            scanResult = gson.fromJson(
                intent?.getStringExtra(SCAN_RESULT_INTENT_EXTRA),
                BluetoothTrackResponseItems::class.java
            )
    }

}