package com.example.ivocaboproject

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

interface ICurrentLoc{
    fun startScanLoc()
    fun stopScanLoc()
}

class CurrentLoc(val ctx:Context):ICurrentLoc {
    var loc=MutableLiveData<LatLng>()
    private lateinit var client: FusedLocationProviderClient
    @SuppressLint("MissingPermission")
    override fun startScanLoc() {
        client= LocationServices.getFusedLocationProviderClient(ctx)
        val locationManager =
            ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGpsEnabled && !isNetworkEnabled) {
            throw ILocationClient.LocationException("GPS is disabled")
        }

        val request = LocationRequest.Builder(2000)
            //.setDurationMillis(10000)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY).build()
        client.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun stopScanLoc() {
        client.removeLocationUpdates(locationCallback)
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.lastOrNull()?.let { location ->
                loc.postValue(LatLng(location.latitude,location.longitude))
                stopScanLoc()
            }
        }
    }

}