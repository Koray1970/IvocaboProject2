package com.example.ivocaboproject.database.localdb

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivocaboproject.AppHelpers
import com.example.ivocaboproject.appHelpers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val repo: DeviceRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val appHandle = AppHelpers()
    var device by mutableStateOf(Device(0, appHelpers.getNOWasSQLDate(), "", "", "", "", ""))
        private set
    var count = viewModelScope.launch { repo.count() }
    private var _list = repo.list()
    var list = MutableLiveData<List<Device>>()
        get() = MutableLiveData<List<Device>>( _list)

    init {
        list.observeForever{
            list=MutableLiveData<List<Device>>( _list)
        }
    }

    fun updateList() {
        viewModelScope.launch {
            list=MutableLiveData<List<Device>>( _list)
        }
    }

    fun insert(device: Device) = viewModelScope.launch {
        repo.insert(device)

    }

    fun update(device: Device) = viewModelScope.launch {
        repo.update(device)

    }

    fun delete(device: Device) = viewModelScope.launch {
        repo.delete(device)

    }
}