package ivo.example.ivocaboproject

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
        val pp= maValue.chunked(2)
        return  pp.joinToString(":").uppercase()//macBuilder.toString().uppercase(Locale.ENGLISH)
    }
    val emailRegex = "(^[a-zA-Z0-9_.]+[@]{1}[a-z0-9]+[\\.][a-z]+\$)"
    fun isValidEmail(email:String):Boolean{
        return email.matches(emailRegex.toRegex())
    }
}