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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val repo: DeviceRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val appHandle = AppHelpers()

    private val uiState = MutableStateFlow(DeviceListViewState(false, emptyList()))

    fun consumableState() = uiState.asStateFlow()

    init {
        fetchDeviceListData()
    }


    fun handleViewEvent(viewEvent: DeviceListViewEvent) {
        when (viewEvent) {
            is DeviceListViewEvent.AddItem -> {
                val currentState = uiState.value
                val items = currentState.devices.toMutableList().apply {
                    add(viewEvent.device)
                }.toList()
                uiState.value = uiState.value.copy(devices = items)
            }
            is DeviceListViewEvent.RemoveItem -> {}
        }
    }

    private fun fetchDeviceListData() {
        viewModelScope.launch {
            delay(2000)
            uiState.value = uiState.value.copy(false, repo.list())
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

data class DeviceListViewState(
    val isloading: Boolean = true,
    val devices: List<Device>,
    val errorMessage: String? = null
)

sealed class DeviceListViewEvent {
    data class AddItem(val device: Device) : DeviceListViewEvent()
    data class RemoveItem(val device: Device) : DeviceListViewEvent()
}