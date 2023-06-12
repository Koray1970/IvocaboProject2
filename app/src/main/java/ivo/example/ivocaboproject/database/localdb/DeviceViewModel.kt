package ivo.example.ivocaboproject.database.localdb

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ivo.example.ivocaboproject.AppHelpers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ivo.example.ivocaboproject.database.EventResultFlags
import ivo.example.ivocaboproject.database.ParseEvents
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val repo: DeviceRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val appHandle = AppHelpers()

    private val uiState = MutableStateFlow(DeviceListViewState(false, emptyList()))
    var getTrackDevicelist = MutableLiveData<List<Device>>()
    fun consumableState() = uiState.asStateFlow()

    init {
        fetchDeviceListData()
        initTrackDeviceList()
    }

    fun syncDeviceList() {
        val parseEvents = ParseEvents()
        val dbrmresult = parseEvents.getDeviceList()
        if (dbrmresult.eventResultFlags == EventResultFlags.SUCCESS) {
            if (!dbrmresult.result.isNullOrEmpty())
                if (dbrmresult.result!!.size > 0) {
                    dbrmresult.result!!.forEach {
                        viewModelScope.launch {
                            repo.insert(it)
                        }
                    }
                }
        }
    }

    fun initTrackDeviceList() {
        viewModelScope.launch {
            var dbTrackList = repo.trackDeviceList()
            if (dbTrackList.size > 0)
                getTrackDevicelist.postValue(dbTrackList)
        }
    }

    fun handleViewEvent(viewEvent: DeviceListViewEvent) {
        when (viewEvent) {
            is DeviceListViewEvent.AddItem -> {
                val currentState = uiState.value
                val items = currentState.devices.toMutableList().apply {
                    add(viewEvent.device)
                }.toList()
                uiState.value = uiState.value.copy(devices = items)
                initTrackDeviceList()
            }

            is DeviceListViewEvent.RemoveItem -> {
                val currentState = uiState.value
                val items = currentState.devices.toMutableList().apply {
                    remove(viewEvent.device)
                }.toList()
                uiState.value = uiState.value.copy(devices = items)
                initTrackDeviceList()
            }
        }
    }

    private fun fetchDeviceListData() {
        viewModelScope.launch {
            delay(2000)
            uiState.value = uiState.value.copy(false, repo.list())
        }
    }

    fun getDeviceDetail(macaddress: String): Device {
        return repo.findbyMacAddress(macaddress)
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

data class DeviceListViewState(
    val isloading: Boolean = true,
    val devices: List<Device>,
    val errorMessage: String? = null,
)

sealed class DeviceListViewEvent {
    data class AddItem(val device: Device) : DeviceListViewEvent()
    data class RemoveItem(val device: Device) : DeviceListViewEvent()
}