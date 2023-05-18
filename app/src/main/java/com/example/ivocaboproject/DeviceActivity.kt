package com.example.ivocaboproject

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ivocaboproject.bluetooth.BluetoothClientItemState
import com.example.ivocaboproject.bluetooth.BluetoothClientService
import com.example.ivocaboproject.bluetooth.IBluetoothClientViewModel
import com.example.ivocaboproject.database.localdb.Device
import com.example.ivocaboproject.database.localdb.DeviceViewModel
import com.example.ivocaboproject.ui.theme.IvocaboProjectTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.ScaleBar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val TAG = DeviceActivity::class.java.simpleName

@AndroidEntryPoint
class DeviceActivity : ComponentActivity() {
    private val deviceViewModel by viewModels<DeviceViewModel>()
    private val appHelpers = AppHelpers()
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val REQUEST_ENABLE_BT = 100

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
                val launcherMultiplePermissions = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissionsMap ->
                    val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
                    if (areGranted) {
                        // Use location
                    } else {
                        // Show dialog
                    }
                }


                var trackBottomSheetState = rememberBottomSheetScaffoldState()
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
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Black,
                                titleContentColor = Color.White
                            ),
                            title = {
                                Column {
                                    Text(text = dbdetails.name)
                                    Text(
                                        text = dbdetails.macaddress,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
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
                    BluetoothPermissionRequest(launcherMultiplePermissions)
                    SetUpBluetooth()
                    DeviceEvents(dbdetails, trackBottomSheetState)
                }

                DeviceTrackPlaceholder(
                    dbdetails,
                    trackBottomSheetState.bottomSheetState
                )
            }
        }
    }

    fun getDateTimeTick(): String {
        var result = ""
        try {
            this.lifecycleScope.launch {
                delay(1000)
                result = com.example.ivocaboproject.appHelpers.getNOWasString()
                return@launch
            }
        } catch (exceptition: Exception) {

        }
        return result
    }

    private fun GoBackEvent() {
        val intent =
            Intent(this@DeviceActivity, MainActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    @Composable
    private fun SetUpBluetooth() {
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.getAdapter()
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        } else {
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                val launcheractivity = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { r ->
                        r.resultCode
                    })
                launcheractivity.launch(enableBtIntent)

                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }
    }

    @Composable
    fun AppNavigator() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "dashboard") {
            composable("dashboard") { Dashboard(navController) }
            composable("registeruser") { RegisterUser(navController) }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun BluetoothPermissionRequest(launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>) {
        val permissions =
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                arrayOf(BLUETOOTH, ACCESS_FINE_LOCATION)
            } else {
                arrayOf(
                    BLUETOOTH_SCAN,
                    BLUETOOTH_ADVERTISE,
                    BLUETOOTH_CONNECT,
                    ACCESS_FINE_LOCATION
                )
            }
        /*val multiplePermissionState = rememberMultiplePermissionsState(
            permissions =
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                listOf(BLUETOOTH, ACCESS_FINE_LOCATION)
            } else {
                listOf(
                    BLUETOOTH_SCAN,
                    BLUETOOTH_ADVERTISE,
                    BLUETOOTH_CONNECT,
                    ACCESS_COARSE_LOCATION
                )
            }
        )*/


        if (
            permissions.all {
                ContextCompat.checkSelfPermission(
                    LocalContext.current.applicationContext,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
        ) {
        } else {
            LaunchedEffect(Unit) {
                delay(4000)
                launcher.launch(permissions)
            }
            // Request permissions

        }
    }
}

private lateinit var latLng: LatLng
private lateinit var camState: CameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceEvents(device: Device, deviceBottomSheetScaffoldState: BottomSheetScaffoldState) {
    val scope = rememberCoroutineScope()
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 64.dp)
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings
            ) {
                //Marker(state = MarkerState(position = broadCastLocationMessage.value))
            }
            ScaleBar(
                modifier = Modifier
                    .padding(top = 5.dp, end = 15.dp)
                    .align(Alignment.BottomStart), cameraPositionState = cameraPositionState
            )
        }
        Column(modifier = Modifier.padding(40.dp, 0.dp)) {
            /*Text(text = DeviceActivity().getDateTimeTick(), color = Color.White, modifier = Modifier)*/
            Row() {
                TextButton(
                    onClick = {
                        scope.launch {
                            deviceBottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    },
                    modifier = Modifier.wrapContentWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.tracking_24),
                            contentDescription = ""
                        )
                        Text(text = stringResource(id = R.string.tracking))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.wrapContentWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.find_24),
                            contentDescription = ""
                        )
                        Text(text = stringResource(id = R.string.find))
                    }
                }

            }
            Row() {

            }
        }

    }

}

@SuppressLint("StateFlowValueCalledInComposition")
@ExperimentalMaterial3Api
@Composable
fun DeviceTrackPlaceholder(
    device: Device,
    bottomSheetState: SheetState,
    iBluetoothClientViewModel: IBluetoothClientViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    var connectionController = 0
    val context = LocalContext.current.applicationContext
    val broadCastDeviceSearchMessage =
        remember { mutableStateOf(BluetoothClientItemState(false, null, null)) }

    val broadcastLocationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        // we will receive data updates in onReceive method.
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra("ivocabosearchresult")) {
                val bluetoothClientItemState =
                    intent.getParcelableExtra<BluetoothClientItemState>("ivocabosearchresult")
                // on below line we are updating the data in our text view.
                broadCastDeviceSearchMessage.value = bluetoothClientItemState!!
            }
        }
    }
    LocalBroadcastManager.getInstance(context).registerReceiver(
        broadcastLocationReceiver, IntentFilter("bluetoothscanresult")
    )

    val viewState = iBluetoothClientViewModel.consumableState().collectAsState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
    val backgroundcolor by animateColorAsState(
        if (!broadCastDeviceSearchMessage.value.errorMassage.isNullOrEmpty()) {
            when (broadCastDeviceSearchMessage.value.errorMassage) {
                "-100" -> Color.LightGray
                else -> Color.Red
            }
        } else
            if (broadCastDeviceSearchMessage.value.isloading) {
                Color.Green
            } else {
                Color.LightGray
            }
    )
    Log.v(TAG, "isLoading : ${broadCastDeviceSearchMessage.value.isloading} ")
    Log.v(TAG, "RSSI: ${broadCastDeviceSearchMessage.value.rssi}")
    Log.v(TAG, "Error: ${broadCastDeviceSearchMessage.value.errorMassage}")

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContainerColor = Color.Black,
        sheetPeekHeight = 0.dp,
        sheetSwipeEnabled = true,
        containerColor = Color.Black,
        sheetContent = {
            Surface(modifier = Modifier.fillMaxSize(), color = backgroundcolor) {
                Text(text = "RSSI : ${broadCastDeviceSearchMessage.value.rssi}")
            }
        }
    ) {}
    Log.v(TAG, " : ${bottomSheetState.currentValue}")
    if (bottomSheetState.currentValue == SheetValue.Expanded) {
        Intent(context, BluetoothClientService::class.java).apply {
            action = BluetoothClientService.ACTION_START
            putExtra("macaddress", device.macaddress)
            context.startService(this)
        }
    } else {
        Intent(context, BluetoothClientService::class.java).apply {
            action = BluetoothClientService.ACTION_START
            putExtra("macaddress", device.macaddress)
            context.stopService(this)
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
