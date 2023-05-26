package com.example.ivocaboproject

import android.content.Context
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationViewModel  :
    ViewModel() {
    private val TAG=LocationViewModel::class.java.simpleName
    private var locationState = MutableLiveData<LatLng>()
    private lateinit var client: FusedLocationProviderClient


    fun getLocationState(): MutableLiveData<LatLng> {
        return locationState
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.lastOrNull()?.let { location ->
                GlobalScope.launch {
                    Log.v(TAG,"Clat : ${location.latitude}  Clog : ${location.longitude}")
                    locationState.postValue(LatLng(location.latitude,location.longitude))
                }
            }
        }
    }



    fun updateLocationState(newLocationState: LatLng) {
        Log.v("LocationViewModel","Lat : ${newLocationState.latitude} Long : ${newLocationState.longitude}")
        locationState.postValue(newLocationState)
    }
}