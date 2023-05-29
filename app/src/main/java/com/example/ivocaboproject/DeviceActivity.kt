@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ivocaboproject

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ivocaboproject.bluetooth.IvocaboleService
import com.example.ivocaboproject.database.EventResultFlags
import com.example.ivocaboproject.database.ParseEvents
import com.example.ivocaboproject.database.localdb.Device
import com.example.ivocaboproject.database.localdb.DeviceViewModel
import com.example.ivocaboproject.deviceevents.FindMyDeviceViewModel
import com.example.ivocaboproject.ui.theme.IvocaboProjectTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.ScaleBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val TAG = DeviceActivity::class.java.simpleName
private val gson = Gson()

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

        setContent {
            IvocaboProjectTheme {
                var trackBottomSheetState = rememberBottomSheetScaffoldState()
                var findDeviceBottomSheetState = rememberBottomSheetScaffoldState()
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
                    SetUpBluetooth()
                    DeviceEvents(dbdetails, trackBottomSheetState, findDeviceBottomSheetState)
                }

                DeviceTrackPlaceholder(
                    dbdetails,
                    trackBottomSheetState.bottomSheetState
                )
                DeviceFindPlaceholder(
                    device = dbdetails,
                    bottomSheetState = findDeviceBottomSheetState.bottomSheetState
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


lateinit var latLng: LatLng
private lateinit var camState: CameraPositionState
private fun permmissions(): List<String> {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
        return listOf(BLUETOOTH, ACCESS_FINE_LOCATION)
    }
    return listOf(BLUETOOTH_SCAN, BLUETOOTH_ADVERTISE, BLUETOOTH_CONNECT, ACCESS_FINE_LOCATION)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionStateInit() {
    val context = LocalContext.current.applicationContext
    val openPermissionDialog = remember { mutableStateOf(false) }
    val permissionsState = rememberMultiplePermissionsState(permissions = permmissions())

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissionsState.launchMultiplePermissionRequest()
                openPermissionDialog.value = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })
    if (openPermissionDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openPermissionDialog.value = false
            },
            icon = { Icon(Icons.Filled.Info, "") },
            title = { Text(text = context.getString(R.string.devicepermissionalerttitle)) },
            text = {
                Column {
                    permissionsState.permissions.forEach {
                        when (it.permission) {
                            ACCESS_FINE_LOCATION -> {
                                when {
                                    it.status.isGranted -> {
                                        Text(text = "Location permission has been granted.")
                                    }

                                    it.status.shouldShowRationale -> {
                                        Text(text = "Location permission is needed.")
                                    }

                                    !(it.status.isGranted && it.status.shouldShowRationale) -> {
                                        Text(text = "Navigate to device settings and enable the location permission.")
                                    }
                                }
                            }

                            BLUETOOTH -> {
                                when {
                                    it.status.isGranted -> {
                                        Text(text = "Bluetooth permission has been granted.")
                                    }

                                    it.status.shouldShowRationale -> {
                                        Text(text = "Bluetooth permission is needed.")
                                    }

                                    !(it.status.isGranted && it.status.shouldShowRationale) -> {
                                        Text(text = "Navigate to device settings and enable the bluetooth permission.")
                                    }
                                }
                            }

                            BLUETOOTH_SCAN -> {
                                when {
                                    it.status.isGranted -> {
                                        Text(text = "Bluetooth scan permission has been granted.")
                                    }

                                    it.status.shouldShowRationale -> {
                                        Text(text = "Bluetooth scan permission is needed.")
                                    }

                                    !(it.status.isGranted && it.status.shouldShowRationale) -> {
                                        Text(text = "Navigate to device settings and enable the bluetooth scan permission.")
                                    }
                                }
                            }

                            BLUETOOTH_ADVERTISE -> {
                                when {
                                    it.status.isGranted -> {
                                        Text(text = "Bluetooth advertise permission has been granted.")
                                    }

                                    it.status.shouldShowRationale -> {
                                        Text(text = "Bluetooth advertise permission is needed.")
                                    }

                                    !(it.status.isGranted && it.status.shouldShowRationale) -> {
                                        Text(text = "Navigate to device settings and enable the bluetooth advertise permission.")
                                    }
                                }
                            }

                            BLUETOOTH_CONNECT -> {
                                when {
                                    it.status.isGranted -> {
                                        Text(text = "Bluetooth connect permission has been granted.")
                                    }

                                    it.status.shouldShowRationale -> {
                                        Text(text = "Bluetooth connect permission is needed.")
                                    }

                                    !(it.status.isGranted && it.status.shouldShowRationale) -> {
                                        Text(text = "Navigate to device settings and enable the bluetooth connect permission.")
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    openPermissionDialog.value = false
                }) {
                    Text(text = context.getString(R.string.ok))
                }
            }
        )


    }
}

@Composable
fun GetLocation(ctx: Context) {
    val currentLoc = CurrentLoc(ctx)
    currentLoc.startScanLoc()
    currentLoc.loc.observe(LocalLifecycleOwner.current) {
        latLng = it
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DeviceEvents(
    device: Device,
    deviceBottomSheetScaffoldState: BottomSheetScaffoldState,
    findDeviceBottomSheetScaffoldState: BottomSheetScaffoldState,
    deviceViewModel: DeviceViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext
    val activity = LocalContext.current as Activity
    if (!context.hasBluetoothPermission())
        PermissionStateInit()

    latLng = LatLng(0.0, 0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 20f)
    }
    //get current location
    val currentLoc = CurrentLoc(context)
    currentLoc.startScanLoc()
    currentLoc.loc.observe(LocalLifecycleOwner.current) {
        Log.v(TAG, "loca : ${gson.toJson(it)}")
        latLng = it
        cameraPositionState.move(
            CameraUpdateFactory.newLatLng(latLng)
        )
    }

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
    var ismissing = false
    if (device.ismissing != null)
        ismissing = device.ismissing!!
    var missingSwitchChecked by remember { mutableStateOf(ismissing) }
    val missingSwitchIcon: (@Composable () -> Unit)? = if (missingSwitchChecked) {
        {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
    } else {
        null
    }

    var notificationSwitchChecked by remember { mutableStateOf(false) }
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
                /*
                * track device event
                * */
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
                /*
                * find device button
                * */
                TextButton(
                    onClick = {
                        scope.launch {
                            findDeviceBottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    },
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
            Row(modifier = Modifier.padding(0.dp, 8.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Switch(checked = notificationSwitchChecked, onCheckedChange = { TODO() })
                    Text(
                        text = stringResource(id = R.string.enablenotifications),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Switch(checked = missingSwitchChecked, onCheckedChange = {
                        missingSwitchChecked = it
                        device.ismissing = null
                        if (missingSwitchChecked) {
                            device.ismissing = true
                            device.latitude = latLng.latitude.toString()
                            device.longitude = latLng.longitude.toString()
                        }
                        val parseEvents = ParseEvents()
                        val dbresult = parseEvents.addRemoveMissingBeacon(device, deviceViewModel)

                        if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {
                            if (device.ismissing == true) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.missingdevicealert),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.missingdevicefindalert),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            if (dbresult.eventResultFlags == EventResultFlags.FAILED) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.missingdevicealerterror),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    }, thumbContent = missingSwitchIcon)
                    Text(
                        text = stringResource(id = R.string.missing),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }

}

@Composable
fun DeviceFindPlaceholder(
    device: Device,
    bottomSheetState: SheetState,
    findMyDeviceViewModel: FindMyDeviceViewModel = hiltViewModel(),
) {
    val context = LocalContext.current.applicationContext
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)


    var currentrssi = remember { MutableLiveData<Int>() }
    val lIntent = Intent(context, IvocaboleService::class.java)
    lIntent.putExtra("macaddress", appHelpers.formatedMacAddress(device.macaddress))
    when (bottomSheetState.currentValue) {
        SheetValue.Expanded -> {
            LaunchedEffect(Unit) {
                context.startService(lIntent)
                IvocaboleService.START_SCAN = true

            }
        }

        else -> {
            LaunchedEffect(Unit) {
                IvocaboleService.START_SCAN = false
                context.stopService(lIntent)
            }
        }
    }
    val screenCurrentWidth = LocalConfiguration.current.screenWidthDp.toFloat()
    val screenCurrentHeight = LocalConfiguration.current.screenHeightDp.toFloat()
    val screenWidth = screenCurrentWidth / 4
    val cirleData = listOf<Color>(
        Color(context.getColor(R.color.orange_100)),
        Color(context.getColor(R.color.orange_90)),
        Color(context.getColor(R.color.orange_80)),
        Color(context.getColor(R.color.orange_70))
    )

    var offsetremember = remember { MutableLiveData<Offset>() }
    currentrssi.observeForever {
        if (it != null) {
            Log.v(TAG,"1")
            when (it) {
                in 30 ..60  -> {
                    Log.v(TAG,"1")
                    offsetremember.postValue(
                        Offset(
                            0f,
                            0f
                        )
                    )

                }

                in 61 ..80 -> {
                    Log.v(TAG,"2")
                    offsetremember.postValue(
                        Offset(
                            0f,
                            (screenCurrentHeight / 3.2).toFloat()
                        )
                    )
                }

                in 81 ..100 -> {
                    Log.v(TAG,"3")
                    offsetremember.postValue(
                        Offset(
                            0f,
                            (screenCurrentHeight / 2.5).toFloat()
                        )
                    )
                }

                else -> {
                    Log.v(TAG,"4")
                    offsetremember.postValue(
                        Offset(
                            0f,
                            (screenCurrentHeight / 1.1).toFloat()
                        )
                    )
                }
            }
        } else {
            offsetremember.postValue(
                Offset(
                    screenCurrentWidth,
                    screenCurrentHeight
                )
            )
        }

    }
    offsetremember.observeForever {
        Log.v(
            TAG,
            "width: $screenCurrentWidth height: $screenCurrentHeight RSSI : ${IvocaboleService.CURRENT_RSSI.value} Offset x : ${it?.x} y : ${it?.y}"
        )
    }
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContainerColor = Color.Black,
        sheetPeekHeight = 0.dp,
        sheetSwipeEnabled = true,
        containerColor = Color.Black,
        sheetContent = {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Magenta) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var i = 4
                        cirleData.forEach {
                            drawCircle(
                                color = it,
                                radius = (screenWidth * i).dp.toPx(),
                                center = Offset(size.width / 2, size.height / 2)
                            )
                            i -= 1
                        }
                    }
                    currentrssi.postValue(IvocaboleService.CURRENT_RSSI.observeAsState().value)
                    Text(
                        text = "${IvocaboleService.CURRENT_RSSI.observeAsState().value}"
                    )
                    offsetremember.observeAsState().value?.let {
                        Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                            .offset(it.x.dp, it.y.dp)
                    }?.let {
                        Box(
                            it
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.t3_icon_32),
                                contentDescription = "Ivocabo Device"
                            )
                        }
                    }
                }
            }
        }
    ) {}
}

@SuppressLint("StateFlowValueCalledInComposition")
@ExperimentalMaterial3Api
@Composable
fun DeviceTrackPlaceholder(
    device: Device,
    bottomSheetState: SheetState
) {
    val scope = rememberCoroutineScope()
    var connectionController = 0
    val context = LocalContext.current.applicationContext

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
    val backgroundcolor by animateColorAsState(Color.LightGray)
    /*if (trackReceiveData.hasError) {
        when (trackReceiveData.errorCode) {
            "-100" -> Color.LightGray
            else -> Color.Red
        }
    } else
        if (trackReceiveData.isScanning == true) {
            Color.Green
        } else {
            Color.LightGray
        }
)*/

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContainerColor = Color.Black,
        sheetPeekHeight = 0.dp,
        sheetSwipeEnabled = true,
        containerColor = Color.Black,
        sheetContent = {
            Surface(modifier = Modifier.fillMaxSize(), color = backgroundcolor) {
                Text(text = "RSSI : ")
            }
        }
    ) {}
    LaunchedEffect(bottomSheetState.currentValue) {
        Log.v(TAG, "bottomSheetState.targetValue : ${bottomSheetState.targetValue}")
        if (bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
//            Intent(context, BluetoothTrackService::class.java).apply {
//                //action = BluetoothTrackService.SERVICE_STOP
//                context.stopService(this)
//            }
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
