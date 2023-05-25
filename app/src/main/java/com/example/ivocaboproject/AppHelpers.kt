package com.example.ivocaboproject

import androidx.compose.ui.text.toUpperCase
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class AppHelpers {
    fun getNOWasDate(): java.util.Date {
        return java.util.Date()
    }

    fun getNOWasString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = java.util.Date()
        return formatter.format(date)
    }

    fun getNOWasTimeStamp(): Long {
        return java.util.Date().time
    }
    fun getNOWasSQLDate():Date{
        val now = System.currentTimeMillis()
        return Date(now)
    }
    fun unformatedMacAddress(macaddress:String):String{
        if(macaddress.contains(":"))
            return macaddress.replace(":","").replace(" ","").uppercase(Locale.ENGLISH).toString()
        if(macaddress.contains("."))
            return macaddress.replace(".","").replace(" ","").uppercase(Locale.ENGLISH).toString()

        return macaddress.replace(" ","").uppercase(Locale.ENGLISH)
    }
    fun formatedMacAddress(maValue:String):String{
        val cleanText = maValue.replace(Regex("[^A-Fa-f0-9]"), "") // Remove non-hexadecimal characters
        val macBuilder = StringBuilder(cleanText)

        // Insert colons at every 2-character interval
        for (i in 2 until macBuilder.length step 3) {
            macBuilder.insert(i, ':')
        }

        return macBuilder.toString().uppercase(Locale.ENGLISH)
    }
}