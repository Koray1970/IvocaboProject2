package com.example.ivocaboproject.database.localdb

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackDeviceViewModel @Inject constructor(private val repo: DeviceRepository) : ViewModel() {
    lateinit var getTrackDevicelist: MutableLiveData<List<Device>>

    init {
        viewModelScope.launch {
            getTrackDevicelist.postValue(repo.trackDeviceList())

        }
    }
}