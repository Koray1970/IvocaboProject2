package com.example.ivocaboproject.database.localdb

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivocaboproject.AppHelpers
import com.example.ivocaboproject.appHelpers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val repo: DeviceRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val appHandle = AppHelpers()
    var device by mutableStateOf(Device(0, appHelpers.getNOWasSQLDate(), "", "", "", "", ""))
        private set
    var count = viewModelScope.launch { repo.count() }

    var list = mutableStateOf(repo.list())
    fun updateList() {
        viewModelScope.launch {
            list = mutableStateOf(repo.list())
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