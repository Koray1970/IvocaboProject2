package com.example.ivocaboproject.database

import android.content.Context
import com.example.ivocaboproject.database.localdb.Device
import com.example.ivocaboproject.database.localdb.DeviceViewModel
import com.example.ivocaboproject.database.localdb.User
import com.example.ivocaboproject.database.localdb.UserViewModel
import com.parse.ParseObject
import com.parse.ParseUser


class ParseEvents {
    fun AddUser(user: User, userViewModel: UserViewModel): EventResult<String> {
        var eventResult = EventResult<String>("")
        try {
            if (ParseUser.getCurrentUser() != null)
                ParseUser.logOut()
            val parseUser = ParseUser()
            parseUser.email = user.email
            parseUser.username = user.username
            parseUser.setPassword(user.password)
            parseUser.signUp()
            if (parseUser.objectId != null) {
                user.objectId = parseUser.objectId.toString()
                userViewModel.addUser(user)
                eventResult.result = user.objectId.toString()
                eventResult.eventResultFlags = EventResultFlags.SUCCESS
            }
        } catch (exception: Exception) {
            eventResult.exception = exception
        }
        return eventResult
    }

    fun SingInUser(userViewModel: UserViewModel): EventResult<Boolean> {
        var eventResult = EventResult<Boolean>(false)
        try {
            var user = userViewModel.getUserDetail
            if (user != null) {
                ParseUser.logIn(user.username, user.password)
                if (ParseUser.getCurrentUser().isAuthenticated) {
                    eventResult.result = true
                    eventResult.eventResultFlags = EventResultFlags.SUCCESS
                }
            }
        } catch (exception: Exception) {
            eventResult.errorcode = "SU100"
            eventResult.exception = exception
        }
        return eventResult
    }

    fun AddEditDevice(device: Device, deviceViewModel: DeviceViewModel): EventResult<Boolean> {
        var eventResult = EventResult<Boolean>(false)
        try {
            val parseuserid = ParseUser.getCurrentUser().objectId
            val parseObject = ParseObject("Beacons")

            var isnew = false
            if (device.objectId.isEmpty())
                isnew = true
            else
                parseObject.objectId=device.objectId

            parseObject.put("User", parseuserid)
            parseObject.put("latitude", device.latitude)
            parseObject.put("longitude", device.longitude)
            parseObject.put("mac", device.macaddress)
            parseObject.put("devicename", device.name)
            parseObject.put("parseUserId", parseuserid)
            parseObject.save()
            if (parseObject.isDataAvailable) {
                if (isnew) {
                    device.objectId = parseObject.objectId
                    deviceViewModel.insert(device)
                } else {
                    deviceViewModel.update(device)
                }
                eventResult.eventResultFlags = EventResultFlags.SUCCESS
                eventResult.result = true
            }

        } catch (exception: Exception) {
            eventResult.exception=exception
        }
        return eventResult
    }
    fun DeleteDevice(device: Device,deviceViewModel: DeviceViewModel):EventResult<Boolean>{
        val eventResult= EventResult<Boolean>(false)
        try{
            val parseuserid = ParseUser.getCurrentUser().objectId
            val parseObject = ParseObject("Beacons")
            parseObject.objectId=device.objectId
            parseObject.put("User", parseuserid)
            parseObject.put("latitude", device.latitude)
            parseObject.put("longitude", device.longitude)
            parseObject.put("mac", device.macaddress)
            parseObject.put("devicename", device.name)
            parseObject.put("parseUserId", parseuserid)
            parseObject.delete()
            if (!parseObject.isDataAvailable) {
                deviceViewModel.updateList()
                eventResult.eventResultFlags = EventResultFlags.SUCCESS
                eventResult.result = true
            }
        }
        catch (exception:Exception){
            eventResult.exception=exception
        }
        return eventResult
    }
}