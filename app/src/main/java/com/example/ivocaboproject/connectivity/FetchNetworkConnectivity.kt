package com.example.ivocaboproject.connectivity

import android.app.Application
import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData

class FetchNetworkConnectivity(private val connectivity: ConnectivityManager) :
    LiveData<InternetConnectionStatus>() {
    constructor(appContext: Application) : this(
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )

    private val networkCallback=object:ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(InternetConnectionStatus.CONNECTED)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(InternetConnectionStatus.DISCONNECTED)
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                postValue(InternetConnectionStatus.CHANGE_TO_WIFI)
            else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                postValue(InternetConnectionStatus.CHANGE_TO_CELL)

            else
                postValue(InternetConnectionStatus.DISCONNECTED)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(InternetConnectionStatus.DISCONNECTED)
        }
    }

    override fun onActive() {
        super.onActive()
        val builder=NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
        connectivity.registerNetworkCallback(builder,networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivity.unregisterNetworkCallback(networkCallback)
    }
}