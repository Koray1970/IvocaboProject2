package ivo.example.ivocaboproject.database

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import ivo.example.ivocaboproject.AppHelpers
import ivo.example.ivocaboproject.appHelpers
import ivo.example.ivocaboproject.database.localdb.Device
import ivo.example.ivocaboproject.database.localdb.DeviceViewModel
import ivo.example.ivocaboproject.database.localdb.User
import ivo.example.ivocaboproject.database.localdb.UserViewModel


class ParseEvents {
    private val TAG = ParseEvents::class.java.simpleName
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
            parseObject.put("devicetype",device.devicetype!!)
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
            val deleteQuery = ParseQuery<ParseObject>("Beacons")
            deleteQuery.whereEqualTo("mac", device.macaddress)
            deleteQuery.findInBackground { objects: List<ParseObject>, e: ParseException? ->
                if (e == null) {
                    val parseDeleteObject = deleteQuery.first
                    parseDeleteObject.deleteInBackground { del ->
                        if (del == null) {
                            deviceViewModel.delete(device)
                            eventResult.eventResultFlags = EventResultFlags.SUCCESS
                            eventResult.result = true
                        }
                    }
                } else {
                    if (deviceViewModel.getDeviceDetail(device.macaddress) != null) {
                        deviceViewModel.delete(device)
                        eventResult.eventResultFlags = EventResultFlags.SUCCESS
                        eventResult.result = true
                    }
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

    fun addRemoveMissingBeacon(
        device: Device,
        deviceViewModel: DeviceViewModel
    ): EventResult<Boolean> {
        val result = EventResult<Boolean>(false)
        try {
            val query = ParseQuery<ParseObject>("MissingBeacons")
            query.whereContains("mac", device.macaddress)
            val queryresult=query.find()
            if(queryresult.size<=0){
                if (device.ismissing == true) {
                    val missingDeviceParseObject = ParseObject("MissingBeacons")
                    missingDeviceParseObject.put(
                        "mupdatedAt",
                        appHelpers.getNOWasSQLDate()
                    )
                    missingDeviceParseObject.put("parseDeviceId", device.objectId)
                    missingDeviceParseObject.put(
                        "User",
                        ParseUser.getCurrentUser().objectId
                    )
                    missingDeviceParseObject.put("latitude", device.latitude.toString())
                    missingDeviceParseObject.put(
                        "longitude",
                        device.longitude.toString()
                    )
                    missingDeviceParseObject.put("Time", appHelpers.getNOWasString())
                    missingDeviceParseObject.put("mac", device.macaddress)
                    missingDeviceParseObject.save()
                    deviceViewModel.update(device)
                    result.eventResultFlags = EventResultFlags.SUCCESS
                    result.result = true
                } else {
                    device.ismissing = null
                    deviceViewModel.update(device)
                    result.eventResultFlags = EventResultFlags.SUCCESS
                    result.result = true
                }
            }
            else{
                //remove missing
                queryresult.first().delete()
                device.ismissing = null
                deviceViewModel.update(device)
                result.eventResultFlags = EventResultFlags.SUCCESS
                result.result = true
            }

        } catch (exception: Exception) {
            result.eventResultFlags = EventResultFlags.FAILED
            result.exception = exception
        }
        return result
    }

    fun CheckAndUpdateMissingDevice(
        macaddresses: List<String>,
        latlang: LatLng,
    ): EventResult<Boolean> {
        val eventResult = EventResult<Boolean>(false)
        try {
            val appHelpers = AppHelpers()
            val query = ParseQuery<ParseObject>("MissingBeacons")
            query.whereContainedIn("mac", macaddresses)
            query.findInBackground { objects: List<ParseObject>, e: ParseException? ->
                if (e == null) {
                    if (objects.size > 0) {
                        objects.forEach {
                            if (!(it.get("latitude").toString()
                                    .take(6) == latlang.latitude.toString().take(6)
                                        && it.get("longitude").toString()
                                    .take(6) == latlang.longitude.toString().take(6))
                            ) {
                                it.put("latitude", latlang.latitude.toString())
                                it.put("longitude", latlang.longitude.toString())
                                it.save()
                                val parseArchiveItem = ParseObject("MissingArchive")
                                parseArchiveItem.put(
                                    "parseDeviceId",
                                    it?.get("parseDeviceId").toString()
                                )
                                parseArchiveItem.put("User", ParseUser.getCurrentUser().objectId)
                                parseArchiveItem.put("latitude", it?.get("latitude").toString())
                                parseArchiveItem.put("time", appHelpers.getNOWasString())
                                parseArchiveItem.put("mac", it?.get("mac").toString())
                                parseArchiveItem.put("longitude", it?.get("longitude").toString())
                                parseArchiveItem.save()
                            }
                        }
                    }
                    Log.i(TAG, "CheckAndUpdateMissingDevice SUCCESS")
                    eventResult.eventResultFlags = EventResultFlags.SUCCESS
                } else {
                    Log.i(TAG, "CheckAndUpdateMissingDevice exception : ${e.message.toString()}")
                    eventResult.errormessage = e.message.toString()
                }
            }
        } catch (exception: Exception) {
            Log.i(TAG, "CheckAndUpdateMissingDevice Gexception : ${exception.message.toString()}")
            eventResult.exception = exception
        }
        return eventResult
    }
}