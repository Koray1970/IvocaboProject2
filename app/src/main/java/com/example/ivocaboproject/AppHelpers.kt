package com.example.ivocaboproject

import java.sql.Date
import java.text.SimpleDateFormat

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
}