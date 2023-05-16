package com.example.ivocaboproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ivocaboproject.database.localdb.Device
import com.example.ivocaboproject.database.localdb.DeviceViewModel
import com.example.ivocaboproject.ui.theme.IvocaboProjectTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.ScaleBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceActivity : ComponentActivity() {
    private val deviceViewModel by viewModels<DeviceViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var macaddress = intent.getStringExtra("macaddress").toString()
        val dbdetails = deviceViewModel.getDeviceDetail(macaddress)

        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            applicationContext.startService(this)
        }
        setContent {
            IvocaboProjectTheme {
                val openDeviceEventFormDialog = remember { mutableStateOf(false) }
                if (dbdetails == null) {
                    openDeviceEventFormDialog.value = true
                }
                if (openDeviceEventFormDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            openDeviceEventFormDialog.value = false
                            GoBackEvent()
                        },
                        icon = { Icon(Icons.Filled.Warning, "") },
                        title = { Text(text = getString(R.string.deviceeventalerttitle)) },
                        text = { Text(text = getString(R.string.deviceeventalerttext)) },
                        confirmButton = {
                            TextButton(onClick = {
                                GoBackEvent()
                            }) {
                                Text(text = getString(R.string.goback))
                            }
                        }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Black,
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White),
                            title = {
                                Column {
                                    Text(text = dbdetails.name)
                                    Text(text = dbdetails.macaddress, style = MaterialTheme.typography.labelSmall)
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    GoBackEvent()
                                },
                                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            }
                        )
                    }
                ) {
                    DeviceEvents(dbdetails)
                }
            }
        }
    }

    private fun GoBackEvent() {
        val intent =
            Intent(this@DeviceActivity, MainActivity::class.java)
        startActivity(intent)
    }

    @Composable
    fun AppNavigator() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "dashboard") {
            composable("dashboard") { Dashboard(navController) }
            composable("registeruser") { RegisterUser(navController) }
        }
    }
}

private lateinit var latLng: LatLng
private lateinit var camState: CameraPositionState

@Composable
fun DeviceEvents(device: Device) {
    val context = LocalContext.current.applicationContext
    val activity = LocalContext.current as Activity
    latLng = LatLng(0.0, 0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 20f)
    }

    val broadCastLocationMessage = remember { mutableStateOf(latLng) }
    val broadcastLocationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        // we will receive data updates in onReceive method.
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
                latLng = LatLng(
                    intent.getDoubleExtra("latitude", 0.0),
                    intent.getDoubleExtra("longitude", 0.0)
                )
                // on below line we are updating the data in our text view.
                broadCastLocationMessage.value = latLng

                cameraPositionState.move(
                    CameraUpdateFactory.newLatLng(
                        broadCastLocationMessage.value
                    )
                )
            }
        }
    }
    LocalBroadcastManager.getInstance(context).registerReceiver(
        broadcastLocationReceiver, IntentFilter("currentlocation")
    )
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isBuildingEnabled = true, isIndoorEnabled = true, isMyLocationEnabled = true
            )
        )
    }
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = true,
                zoomControlsEnabled = false,
                zoomGesturesEnabled = true,
                rotationGesturesEnabled = true
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 64.dp)) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings
            ) {
                Marker(state = MarkerState(position = broadCastLocationMessage.value))
            }
            ScaleBar(
                modifier = Modifier
                    .padding(top = 5.dp, end = 15.dp)
                    .align(Alignment.BottomStart), cameraPositionState = cameraPositionState
            )
        }
        Column() {
            Row() {
                IconButton(onClick = { /*TODO*/ },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                    Icon(painter = painterResource(id = R.drawable.tracking_24), contentDescription ="" )
                    Text(text = stringResource(id = R.string.tracking))
                }
            }
        }
    }

}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IvocaboProjectTheme {
        Greeting("Android")
    }
}*/
