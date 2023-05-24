package com.example.ivocaboproject.database

import android.util.Log
import com.example.ivocaboproject.AppHelpers
import com.example.ivocaboproject.appHelpers
import com.example.ivocaboproject.database.localdb.Device
import com.example.ivocaboproject.database.localdb.DeviceViewModel
import com.example.ivocaboproject.database.localdb.User
import com.example.ivocaboproject.database.localdb.UserViewModel
import com.google.android.gms.maps.model.LatLng
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser


class ParseEvents {
    private val TAG=ParseEvents::class.java.simpleName
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
                parseObject.objectId = device.objectId

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
            eventResult.exception = exception
        }
        return eventResult
    }

    fun DeleteDevice(device: Device, deviceViewModel: DeviceViewModel): EventResult<Boolean> {
        val eventResult = EventResult<Boolean>(false)
        try {
            val parseuserid = ParseUser.getCurrentUser().objectId
            val parseObject = ParseObject("Beacons")
            parseObject.objectId = device.objectId
            parseObject.put("User", parseuserid)
            parseObject.put("latitude", device.latitude)
            parseObject.put("longitude", device.longitude)
            parseObject.put("mac", device.macaddress)
            parseObject.put("devicename", device.name)
            parseObject.put("parseUserId", parseuserid)
            parseObject.delete()
            if (!parseObject.isDataAvailable) {
                deviceViewModel.delete(device)
                eventResult.eventResultFlags = EventResultFlags.SUCCESS
                eventResult.result = true
            } else {
                if (deviceViewModel.getDeviceDetail(device.macaddress) != null) {
                    deviceViewModel.delete(device)
                    eventResult.eventResultFlags = EventResultFlags.SUCCESS
                    eventResult.result = true
                }
            }
        } catch (exception: Exception) {
            if (deviceViewModel.getDeviceDetail(device.macaddress) != null) {
                deviceViewModel.delete(device)
                eventResult.eventResultFlags = EventResultFlags.SUCCESS
                eventResult.result = true
            } else {
                eventResult.exception = exception
            }
        }
        return eventResult
    }

    fun CheckAndUpdateMissingDevice(
        macaddresses: List<String>,
        latlang: LatLng,
    ): EventResult<Boolean> {
        val eventResult = EventResult<Boolean>(false)
        try {
            val appHelpers= AppHelpers()
            val query = ParseQuery<ParseObject>("MissingBeacons")
            query.whereContainsAll("mac", macaddresses)
            query.findInBackground { objects: List<ParseObject>, e: ParseException? ->
                if (e == null) {
                    objects.forEach {
                        it.put("latitude", latlang.latitude.toString())
                        it.put("longitude", latlang.longitude.toString())
                        it.saveInBackground()
                        val parseArchiveItem=ParseObject("MissingArchive")
                        parseArchiveItem.put("parseDeviceId",it?.get("parseDeviceId").toString())
                        parseArchiveItem.put("User",ParseUser.getCurrentUser().objectId)
                        parseArchiveItem.put("latitude",it?.get("latitude").toString())
                        parseArchiveItem.put("time",appHelpers.getNOWasSQLDate())
                        parseArchiveItem.put("mac",it?.get("mac").toString())
                        parseArchiveItem.put("longitude",it?.get("longitude").toString())
                        parseArchiveItem.saveInBackground()
                    }
                    Log.i(TAG,"CheckAndUpdateMissingDevice SUCCESS")
                    eventResult.eventResultFlags = EventResultFlags.SUCCESS
                } else {
                    Log.i(TAG,"CheckAndUpdateMissingDevice exception : ${e.message.toString()}")
                    eventResult.errormessage = e.message.toString()
                }
            }
        } catch (exception: Exception) {
            Log.i(TAG,"CheckAndUpdateMissingDevice Gexception : ${exception.message.toString()}")
            eventResult.exception = exception
        }
        return eventResult
    }
}