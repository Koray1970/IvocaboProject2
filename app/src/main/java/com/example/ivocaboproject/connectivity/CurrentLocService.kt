package com.example.ivocaboproject.connectivity

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.ivocaboproject.ILocationClient
import com.example.ivocaboproject.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CurrentLocService : Service() {
    private val TAG=CurrentLocService::class.java.simpleName
    private lateinit var viewModel: LocationViewModel
    private lateinit var client: FusedLocationProviderClient
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(LocationViewModel::class.java)
        client=LocationServices.getFusedLocationProviderClient(applicationContext)
        val locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.lastOrNull()?.let { location ->
                GlobalScope.launch {
                    Log.v(TAG,"Clat : ${location.latitude}  Clog : ${location.longitude}")
                    viewModel.updateLocationState(LatLng(location.latitude,location.longitude))
                }
            }
        }
    }

    override fun onDestroy() {
        client.removeLocationUpdates(locationCallback)
        stopSelf()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }
}