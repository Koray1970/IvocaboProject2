package com.example.ivocaboproject.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivocaboproject.ILocationClient
import com.example.ivocaboproject.hasBluetoothPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BluetoothClientItemState(
    val isloading: Boolean = true,
    val rssi: Int? = null,
    val errorMassage: String? = null
)

interface IBluetoothClient {
    fun getDeviceTrack(bluetoothAdapter: BluetoothAdapter,macaddress: String): Flow<BluetoothClientItemState>
    class BluetoothClientException(message: String) : Exception()
}

class IBluetoothClientRepository @Inject constructor() : IBluetoothClient {
    override fun getDeviceTrack(bluetoothAdapter: BluetoothAdapter,macaddress: String): Flow<BluetoothClientItemState> =
        getDeviceTrack(bluetoothAdapter,macaddress)


}

@HiltViewModel
class IBluetoothClientViewModel @Inject constructor(
    private val repo: IBluetoothClientRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uiState = MutableStateFlow(BluetoothClientItemState(false, null, null))
    fun consumableState() = uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1000)
            uiState.value
        }
    }

    fun handleViewEvent(viewEvent: IBluetoothClientViewEvent) {
        val currentState = uiState.value
        val items = currentState.apply { viewEvent }
        uiState.value = uiState.value.copy(true, items.rssi, items.errorMassage)
    }
}

sealed class IBluetoothClientViewEvent {
    data class AddItem(val itemState: BluetoothClientItemState) : IBluetoothClientViewEvent()
}


class BluetoothClient(private val context: Context) : IBluetoothClient {
    override fun getDeviceTrack(bluetoothAdapter: BluetoothAdapter, macaddress: String): Flow<BluetoothClientItemState> {
        return callbackFlow {
            if (context.hasBluetoothPermission()) {
                throw IBluetoothClient.BluetoothClientException("Missing Bluetooth Permission")
            }
            val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        }
    }
}