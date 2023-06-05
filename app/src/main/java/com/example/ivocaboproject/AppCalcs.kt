package com.example.ivocaboproject

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import kotlin.math.pow

class AppCalcs {
    private val TAG=AppCalcs::class.java.simpleName
    private val maxRssi = 120
    private val minRssi = 45
    private val rangeDiff = maxRssi - minRssi
    private val topofbottomsheet = 72 + 48
    var innerHeightSurface = 0f
    var innerRange = 0f
    var screenCurrentWidth = 0
    var screenCurrentHeight = 0

    fun setOffsetY(sHeight:Float,currentRssi: Int): Float {
        innerHeightSurface = (sHeight - topofbottomsheet)
        Log.v(TAG,"innerHeightSurface $innerHeightSurface")
        innerRange = (innerHeightSurface / rangeDiff).toFloat()
        Log.v(TAG,"innerRange $innerRange")
        val iCurrentRssi = currentRssi - minRssi
        Log.v(TAG,"iCurrentRssi $iCurrentRssi")
        Log.v(TAG,"Curr :  ${innerHeightSurface-(iCurrentRssi * innerRange)}")
        return innerHeightSurface-(iCurrentRssi * innerRange)
    }
    fun getRssiDistance(rssi:Int):String{
        val result = 10.0.pow((-50 - (rssi)) / (10 * 2).toDouble())
        return String.format("%.0f", result)
    }
}