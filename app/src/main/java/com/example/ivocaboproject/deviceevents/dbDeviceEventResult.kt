package com.example.ivocaboproject.deviceevents

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class dbDeviceEventResult(val rssi: Int?, val errorCode: Int?)

@HiltViewModel
class FindMyDeviceViewModel @Inject constructor() : ViewModel() {
    private var eventResult = MutableLiveData<dbDeviceEventResult>()

    fun updateEventResult(dbDeviceEventResult: dbDeviceEventResult) {
        eventResult.postValue(dbDeviceEventResult)
    }

    fun getEventResult(): MutableLiveData<dbDeviceEventResult> {
        return eventResult
    }
}
