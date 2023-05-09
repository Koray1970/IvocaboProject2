package com.example.ivocaboproject

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

interface ILocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String) : Exception()
}

class ILocationClientRepository @Inject constructor() : ILocationClient {
    override fun getLocationUpdates(interval: Long): Flow<Location> = getLocationUpdates(interval)
}
@HiltViewModel
class ILocationClientViewModel @Inject constructor(
    private val repo: ILocationClientRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var latitude by mutableStateOf(0.0)
    var longitude by mutableStateOf(0.0)
    var location = repo.let { it. } .getLocationUpdates(10000L).onEach { i ->
        latitude = i.latitude
        longitude = i.longitude
    }
        private set

}