package ivo.example.ivocaboproject.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.MutableLiveData

class AppInternetConnectivity {
    private val TAG=AppInternetConnectivity::class.java.simpleName
    private lateinit var _context:Context
    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    constructor(context:Context){
        _context=context
        val connectivityManager =  context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        Log.v(TAG,"connectivityManager.isActiveNetworkMetered : ${connectivityManager.isActiveNetworkMetered}")
        INTERNET_CONNECTION_STATUS.postValue(connectivityManager.isActiveNetworkMetered)
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            INTERNET_CONNECTION_STATUS.postValue(true)
            Log.v(TAG,"INTERNET_CONNECTION_STATUS : true")
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            INTERNET_CONNECTION_STATUS.postValue(false)
            Log.v(TAG,"INTERNET_CONNECTION_STATUS : false")
            //Toast.makeText(_context,_context.getString(R.string.internetdisconnected),Toast.LENGTH_LONG).show()
        }
    }
    companion object{
        var INTERNET_CONNECTION_STATUS=MutableLiveData<Boolean>(true)
    }

}