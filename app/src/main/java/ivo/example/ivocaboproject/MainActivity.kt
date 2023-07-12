package ivo.example.ivocaboproject

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.ScaleBar
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import ivo.example.ivocaboproject.bluetooth.BleTrackerService
import ivo.example.ivocaboproject.bluetooth.IvocaboFetcher
import ivo.example.ivocaboproject.connectivity.FetchNetworkConnectivity
import ivo.example.ivocaboproject.connectivity.InternetConnectionStatus
import ivo.example.ivocaboproject.database.EventResultFlags
import ivo.example.ivocaboproject.database.ParseEvents
import ivo.example.ivocaboproject.database.localdb.Device
import ivo.example.ivocaboproject.database.localdb.DeviceListViewEvent
import ivo.example.ivocaboproject.database.localdb.DeviceListViewState
import ivo.example.ivocaboproject.database.localdb.DeviceViewModel
import ivo.example.ivocaboproject.database.localdb.TrackArchiveViewModel
import ivo.example.ivocaboproject.database.localdb.User
import ivo.example.ivocaboproject.database.localdb.UserViewModel
import ivo.example.ivocaboproject.ui.theme.IvocaboProjectTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val appHelpers = AppHelpers()


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val parseEvents = ParseEvents()
    private val gson = Gson()
    private lateinit var nDevice: Device

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //context.deleteDatabase("ivocabo.db")
        //ParseUser.logOut()
        val permissions = if (Build.VERSION.SDK_INT <= 30) {
            listOf(
                //android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            listOf(
                /*android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.BLUETOOTH_CONNECT,*/
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        val deviceViewModel: DeviceViewModel by viewModels()
        val trackArchiveViewModel: TrackArchiveViewModel by viewModels()
        val cld = FetchNetworkConnectivity(application)
        cld.observe(this) { isConnected ->
            if (isConnected == InternetConnectionStatus.DISCONNECTED) {
                Toast.makeText(
                    applicationContext, getString(R.string.internetdisconnected), Toast.LENGTH_LONG
                ).show()
            }
        }

        setContent {
            IvocaboProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val locationPermissionsState = rememberMultiplePermissionsState(permissions)
                    val approximateDialog = remember { mutableStateOf(false) }
                    val requestDialog = remember { mutableStateOf(false) }
                    val deniedDialog = remember { mutableStateOf(false) }

                    if (locationPermissionsState.allPermissionsGranted) {

                        //start::Track Notification Broadcastreceiver
                        val trackNotificationIntent = remember { mutableStateOf("") }
                        val trackNotificationOpenDialog = remember { mutableStateOf(false) }
                        SystemBroadcastReceiver(systemAction = "hasTrackNotification") { receiverState ->
                            val action = receiverState?.action ?: return@SystemBroadcastReceiver
                            if (action == "hasTrackNotification") {
                                trackNotificationOpenDialog.value = true
                                nDevice = gson.fromJson(
                                    receiverState.getStringExtra("lostdevice"),
                                    Device::class.java
                                )
                                trackNotificationIntent.value =
                                    "${nDevice.name} \n ${receiverState.getStringExtra("detail")}"
                            }
                        }
                        if (trackNotificationOpenDialog.value) {
                            AlertDialog(onDismissRequest = {
                                trackNotificationOpenDialog.value = false
                            },
                                title = { Text(text = getString(R.string.notificationtitle)) },
                                text = { Text(text = trackNotificationIntent.value) },
                                confirmButton = {
                                    TextButton(onClick = {
                                        trackNotificationOpenDialog.value = false
                                    }) {
                                        Text(text = "Oki")
                                    }
                                }
                            )
                        }
                        //end::Track Notification Broadcastreceiver
                        AppNavigator()

                        BleTrackerService.IS_SEVICE_RUNNING.observeForever {
                            if (!it) {
                                trackServiceIntent =
                                    Intent(applicationContext, BleTrackerService::class.java)
                                applicationContext.startService(trackServiceIntent)
                            }
                        }

                    } else {
                        Column {
                            val allPermissionsRevoked =
                                locationPermissionsState.permissions.size ==
                                        locationPermissionsState.revokedPermissions.size

                            if (!allPermissionsRevoked) {
                                approximateDialog.value = true
                            } else if (locationPermissionsState.shouldShowRationale) {
                                deniedDialog.value = true
                            } else {
                                requestDialog.value = true
                            }
                            if (requestDialog.value) {
                                AlertDialog(onDismissRequest = { requestDialog.value = false },
                                    title = { Text(text = getString(R.string.prm_locationtitle)) },
                                    text = { Text(text = getString(R.string.prm_locationrequest)) },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            locationPermissionsState.launchMultiplePermissionRequest()
                                            requestDialog.value = false
                                        }) {
                                            Text(text = getString(R.string.prm_locationbtn))
                                        }
                                    }
                                )
                            }
                            if (approximateDialog.value) {
                                AlertDialog(onDismissRequest = { approximateDialog.value = false },
                                    title = { Text(text = getString(R.string.prm_locationtitle)) },
                                    text = { Text(text = getString(R.string.prm_locationrequestonbehalf)) },
                                    confirmButton = {

                                        TextButton(onClick = {
                                            locationPermissionsState.launchMultiplePermissionRequest()
                                            approximateDialog.value = false
                                        }) {
                                            Text(text = getString(R.string.prm_locationbtnallowprecise))
                                        }
                                    }
                                )
                            }
                            if (deniedDialog.value) {
                                AlertDialog(onDismissRequest = { deniedDialog.value = false },
                                    title = { Text(text = getString(R.string.prm_locationtitle)) },
                                    text = { Text(text = getString(R.string.prm_locationrequestdenied)) },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            locationPermissionsState.launchMultiplePermissionRequest()
                                            deniedDialog.value = false
                                        }) {
                                            Text(text = getString(R.string.prm_locationbtn))
                                        }
                                    }
                                )
                            }
                        }
                    }
                    //

                }
            }
        }
    }


    companion object {
        lateinit var trackServiceIntent: Intent
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "registeruser") {
        composable("dashboard") { Dashboard(navController) }
        composable("registeruser") { RegisterUser(navController) }
        composable("signin") { SignIn(navController) }
        composable("resetpassword") { ResetPassword(navController) }
        composable("settings") { Settings() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
var deviceFormSheetState = mutableStateOf(SheetState(false, SheetValue.PartiallyExpanded))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationBar(navController: NavController) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current.applicationContext
    val items = listOf(
        Pair(context.getString(R.string.menu_1), Pair("dashboard", R.drawable.baseline_home_24)),
        //Pair(context.getString(R.string.menu_2), Pair("", R.drawable.baseline_emoji_people_24)),
        Pair(context.getString(R.string.menu_3), Pair("settings", R.drawable.baseline_settings_24))
    )
    BottomAppBar(
        actions = {
            items.forEachIndexed { _, s ->
                IconButton(onClick = { navController.navigate(s.second.first) }) {
                    Icon(
                        painter = painterResource(id = s.second.second),
                        contentDescription = s.first
                    )
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    scope.launch {
                        deviceFormSheetState.value.expand()
                    }
                },
                //containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.addnewdevice),
                    //tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        })
}

//private lateinit var latLng: LatLng
private val gson: Gson = Gson()

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    navController: NavController,
    userviewModel: UserViewModel = hiltViewModel(),
    deviceViewModel: DeviceViewModel = hiltViewModel(),
) {
    val context = LocalContext.current.applicationContext

    deviceViewModel.trackDeviceItems.observeForever {
        BleTrackerService.MACADDRESS_LIST.postValue(it)
    }

    if (userviewModel.count <= 0) {
        navController.navigate("registeruser")
    } else {
        if (!ParseUser.getCurrentUser().isAuthenticated) {
            val user = userviewModel.getUserDetail
            val parseEvents = ParseEvents()
            val dbresult = parseEvents.SingInUser(user.email, user.password, userviewModel)
            if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {
                Toast.makeText(
                    context, context.getString(R.string.userisauthenticated), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    var latLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 16f)
    }

    //get current location from CurrentLoc Class
    val currentLoc = CurrentLoc(context)
    currentLoc.startScanLoc()
    currentLoc.loc.observe(LocalLifecycleOwner.current) {
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
    //val deviceformsheetState = rememberBottomSheetScaffoldState()
    val deviceViewState = deviceViewModel.consumableState().collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { AppNavigationBar(navController) },
        //containerColor = MaterialTheme.colorScheme.tertiaryContainer,
    ) {

        LaunchedEffect(Unit) {
            delay(2000L)
            while (true) {
                val data = Data.Builder()
                data.putString("latlong", gson.toJson(latLng))
                val workRequest = OneTimeWorkRequestBuilder<IvocaboFetcher>()
                    .setInputData(data.build())
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
                //delay(240000L) //4 minute
                delay(900000L) //15 minute
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = mapUiSettings
                ) {
                    Marker(state = MarkerState(position = latLng))
                }
                ScaleBar(
                    modifier = Modifier
                        .padding(top = 5.dp, end = 15.dp)
                        .align(Alignment.BottomStart), cameraPositionState = cameraPositionState
                )
            }
            //device list
            DeviceList(deviceViewState)
        }

    }
    DeviceForm()

}

private lateinit var lIntent: Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceList(
    state: State<DeviceListViewState>,
    deviceViewModel: DeviceViewModel = hiltViewModel(),
) {
    val context = LocalContext.current.applicationContext
    val parseEvents = ParseEvents()
    val listState = rememberLazyListState()

    val txtitemdelete = stringResource(id = R.string.devicedelete)
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp, 10.dp),
            text = stringResource(id = R.string.devicelisttitle),
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )
    }
    var deviceList = remember { mutableListOf<Device>() }
    deviceViewModel.loadDeviceList()
    if (deviceViewModel.livedataDevicelist.observeAsState().value?.isNotEmpty() == true)
        deviceList = deviceViewModel.livedataDevicelist.observeAsState().value!!.toMutableList()
    val isLoading by deviceViewModel.isloading.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = deviceViewModel::loadDeviceList,
        indicator = { state, refreshTrigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = refreshTrigger,
                backgroundColor = Color.Green,
                contentColor = Color.DarkGray
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            state = listState,
            userScrollEnabled = true
        ) {
            itemsIndexed(deviceList) { _, item ->
                val dismissState = rememberDismissState(confirmValueChange = {
                    if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                        val dbresult = parseEvents.DeleteDevice(item, deviceViewModel)
                        if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {
                            deviceViewModel.handleViewEvent(
                                DeviceListViewEvent.RemoveItem(device = item)
                            )
                            Toast.makeText(context, txtitemdelete, Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                })
                var deviceicon = -1
                when (item.devicetype) {
                    1 -> deviceicon = R.drawable.t3_icon_32
                    2 -> deviceicon = R.drawable.e9_icon_32
                }
                SwipeToDismiss(state = dismissState, background = {
                    dismissState.dismissDirection ?: return@SwipeToDismiss
                    DeviceSwipeBackground(dismissState = dismissState)
                }, dismissContent = {
                    Card(
                        onClick = {
                            scope.launch {
                                val intent = Intent(context, DeviceActivity::class.java)
                                intent.putExtra("macaddress", item.macaddress)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            }
                        },

                        shape = RoundedCornerShape(0.dp),
                        elevation = CardDefaults.cardElevation(1.dp, 1.dp, 1.dp, 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black,
                            contentColor = Color.White,
                        )
                    ) {
                        ListItem(
                            /*colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),*/
                            leadingContent = {
                                Image(
                                    painter = painterResource(id = deviceicon),
                                    contentDescription = null
                                )
                            },
                            headlineContent = {
                                Text(
                                    item.name,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp
                                    )
                                )
                            },
                            supportingContent = {
                                Text(
                                    item.macaddress,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Light
                                    )
                                )
                            }, trailingContent = {
                                Row() {
                                    if (item.istracking != null) {
                                        Icon(
                                            modifier = Modifier.width(20.dp),
                                            imageVector = ImageVector.vectorResource(id = R.drawable.gps_9084630),
                                            contentDescription = "Tracking"
                                        )
                                        //Spacer(modifier = Modifier.width(10.dp))
                                    }
                                    if (item.ismissing != null) {
                                        Icon(
                                            modifier = Modifier.width(20.dp),
                                            imageVector = ImageVector.vectorResource(id = R.drawable.location_4542804),
                                            contentDescription = "Missing"
                                        )
                                    }
                                }
                            }
                        )
                        Divider()
                    }
                })
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialTheme.colorScheme.secondary
            DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.error
            DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error
        }
    )

    val icon: Painter = when (direction) {
        DismissDirection.StartToEnd -> painterResource(id = R.drawable.baseline_delete_24)//painterResource(id = R.drawable.baseline_edit_note_24)
        DismissDirection.EndToStart -> painterResource(id = R.drawable.baseline_delete_24)
    }
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 1f else 1.75f
    )
    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(painter = icon, "", Modifier.scale(scale))
    }
}

lateinit var displayiconDesc: String
private lateinit var logodescription: String


@Composable
fun RegisterUser(
    navController: NavController, userviewModel: UserViewModel = hiltViewModel(),
) {
    val context = LocalContext.current.applicationContext
    logodescription = context.getString(R.string.logodescription)
    if (ParseUser.getCurrentUser() != null) {
        navController.navigate("dashboard")
    } else {
        GetLocation(context)
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {

            }) {

            Image(
                modifier = Modifier
                    .width(110.dp)
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(0.dp, 10.dp),
                painter = painterResource(id = R.drawable.ivocabo_logo_vecappicon),
                contentDescription = logodescription
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
                text = stringResource(id = R.string.rg_title),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.rg_subtitle),
                style = TextStyle(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            )

            var txtrgusername by rememberSaveable { mutableStateOf("") }
            val isusernameVisible by remember { derivedStateOf { txtrgusername.isNotBlank() } }
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                onValueChange = { txtrgusername = it },
                label = { Text(text = stringResource(id = R.string.rg_username)) },
                supportingText = { Text(text = stringResource(id = R.string.rg_usernamesupporting)) },
                value = txtrgusername,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text
                ),
                trailingIcon = {
                    if (isusernameVisible) {
                        IconButton(onClick = { txtrgusername = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = context.getString(R.string.rg_usernameremoving)
                            )
                        }
                    }
                })
            var txtrgemail by rememberSaveable { mutableStateOf("") }
            val isemailVisible by remember { derivedStateOf { txtrgemail.isNotBlank() } }
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                onValueChange = { txtrgemail = it },
                label = { Text(text = stringResource(id = R.string.email)) },
                supportingText = { Text(text = stringResource(id = R.string.emailsupporting)) },
                value = txtrgemail,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Email
                ),
                trailingIcon = {
                    if (isemailVisible) {
                        IconButton(onClick = { txtrgemail = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = context.getString(R.string.emailremoving)
                            )
                        }
                    }
                })
            var txtrgpassword by rememberSaveable { mutableStateOf("") }
            var ispasswordVisible by remember { mutableStateOf(false) }

            val icon = if (ispasswordVisible) {
                displayiconDesc = stringResource(id = R.string.rg_passwordhide)
                painterResource(id = R.drawable.baseline_visibility_2480)
            } else {
                displayiconDesc = stringResource(id = R.string.rg_passworddisplay)
                painterResource(id = R.drawable.baseline_visibility_off_24)
            }
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                onValueChange = { txtrgpassword = it },
                label = { Text(text = stringResource(id = R.string.rg_password)) },
                supportingText = { Text(text = stringResource(id = R.string.rg_passwordsupporting)) },
                value = txtrgpassword,
                visualTransformation = if (ispasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        ispasswordVisible = !ispasswordVisible
                    }) {
                        Icon(
                            painter = icon, contentDescription = displayiconDesc
                        )
                    }
                })
            Text(
                modifier = Modifier.padding(0.dp, 10.dp),
                text = stringResource(id = R.string.rg_warning),
                style = TextStyle(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                FilledTonalButton(
                    onClick = { navController.navigate("signin") },
                ) {
                    Text(text = stringResource(id = R.string.signin))
                }
                Spacer(modifier = Modifier.weight(1f))
                FilledTonalButton(
                    onClick = {
                        val parseEvents = ParseEvents()
                        val user = User(
                            0,
                            appHelpers.getNOWasSQLDate(),
                            txtrgusername,
                            txtrgemail,
                            txtrgpassword,
                            null,
                            null
                        )
                        val dbresult = parseEvents.AddUser(user, userviewModel)
                        if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {
                            txtrgusername = ""
                            txtrgemail = ""
                            txtrgpassword = ""
                            navController.navigate("dashboard")
                        }
                    },
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
            }

        }
    }
}

@Composable
fun SignIn(
    navController: NavController,
    userviewModel: UserViewModel = hiltViewModel(),
    deviceViewModel: DeviceViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext

    var progressState = remember { mutableStateOf(false) }
    AppProgress(progressState)

    logodescription = context.getString(R.string.logodescription)
    var txtsiUserNameErrorState by remember { mutableStateOf(false) }
    var txtsiusername by rememberSaveable { mutableStateOf("") }
    val issiusernameBlank by remember { derivedStateOf { txtsiusername.isNotBlank() } }

    var txtsiPasswordErrorState by remember { mutableStateOf(false) }
    var txtsipassword by remember { mutableStateOf("") }
    var ispasswordsiVisible by remember { mutableStateOf(false) }
    val icon = if (ispasswordsiVisible) painterResource(id = R.drawable.baseline_visibility_2480)
    else painterResource(id = R.drawable.baseline_visibility_off_24)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            modifier = Modifier
                .width(110.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .padding(0.dp, 10.dp),
            painter = painterResource(id = R.drawable.ivocabo_logo_vecappicon),
            contentDescription = logodescription
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp),
            text = stringResource(id = R.string.signin),
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                if (it.length > 0) txtsiUserNameErrorState = false
                txtsiusername = it
            },
            label = { Text(text = stringResource(id = R.string.rg_username)) },
            value = txtsiusername,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Text
            ),
            trailingIcon = {
                if (issiusernameBlank) {
                    IconButton(onClick = { txtsiusername = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear, contentDescription = "Clear"
                        )
                    }
                }
            },
            isError = txtsiUserNameErrorState
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                if (it.length > 0) txtsiPasswordErrorState = false
                txtsipassword= it
            },
            label = { Text(text = stringResource(id = R.string.rg_password)) },
            value = txtsipassword,
            visualTransformation = if (ispasswordsiVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                autoCorrect = false
            ),
            trailingIcon = {
                IconButton(onClick = {
                    ispasswordsiVisible = !ispasswordsiVisible
                }) {
                    Icon(
                        painter = icon, contentDescription = "Visibility Icon"
                    )
                }
            },
            isError = txtsiPasswordErrorState
        )
        Column(
            modifier = Modifier
                .padding(10.dp, 15.dp)
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = stringResource(id = R.string.resetpasswordwarning),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            )
            TextButton(
                onClick = { navController.navigate("resetpassword") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.buttonresetpassword),
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            FilledTonalButton(
                onClick = { navController.navigate("registeruser") },
            ) {
                Text(text = stringResource(id = R.string.goback))
            }
            Spacer(modifier = Modifier.weight(1f))
            FilledTonalButton(
                onClick = {
                    scope.launch {
                        progressState.value = true
                        delay(2000)
                        if (txtsiusername.isNotEmpty() && txtsipassword.isNotEmpty()) {
                            val parseEvents = ParseEvents()
                            val dbresult =
                                parseEvents.SingInUser(txtsiusername, txtsipassword, userviewModel)
                            if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {
                                deviceViewModel.syncDeviceList()
                                delay(2800L)
                                navController.navigate("dashboard")
                            } else {
                                progressState.value = false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.checksingincredentials),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            progressState.value = false
                            txtsiUserNameErrorState =
                                if (txtsiusername.isNullOrBlank()) true else false
                            txtsiPasswordErrorState =
                                if (txtsipassword.isNullOrBlank()) true else false
                            Toast.makeText(
                                context,
                                context.getString(R.string.formelementisempty),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
            ) {
                Text(text = stringResource(id = R.string.signin))
            }
        }
    }

}

lateinit var msg: String

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ResetPassword(navController: NavController) {
    val context = LocalContext.current.applicationContext
    logodescription = context.getString(R.string.logodescription)


    val scope = rememberCoroutineScope()
    val alertbarHostState = remember { SnackbarHostState() }
    var txtrpemail by rememberSaveable { mutableStateOf("") }
    val isrpemailVisible by remember { derivedStateOf { txtrpemail.isNotBlank() } }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val confirmOpenDialog = remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = alertbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        content = { innerPadding ->
            if (confirmOpenDialog.value) {
                AlertDialog(onDismissRequest = { confirmOpenDialog.value = false },
                    content = {
                        Surface(
                            modifier = Modifier
                                .wrapContentWidth()
                                .wrapContentHeight(),
                            shape = MaterialTheme.shapes.large,
                            tonalElevation = AlertDialogDefaults.TonalElevation
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(id = R.string.resetpasswordsuccessful)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                TextButton(
                                    onClick = {
                                        confirmOpenDialog.value = false
                                        navController.navigate("signin")
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(stringResource(id = R.string.gotosignin))
                                }
                            }
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = Color.Black),
            ) {
                Image(
                    modifier = Modifier
                        .width(110.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(0.dp, 10.dp),
                    painter = painterResource(id = R.drawable.ivocabo_logo_vecappicon),
                    contentDescription = logodescription
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp),
                    text = stringResource(id = R.string.resetpasswordformtitle),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    onValueChange = { txtrpemail = it },
                    label = { Text(text = stringResource(id = R.string.email)) },
                    value = txtrpemail,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Email
                    ),
                    trailingIcon = {
                        if (isrpemailVisible) {
                            IconButton(onClick = { txtrpemail = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear, contentDescription = "Clear"
                                )
                            }
                        }
                    })
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    FilledTonalButton(
                        onClick = { navController.navigate("signin") },
                    ) {
                        Text(text = stringResource(id = R.string.goback))
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    FilledTonalButton(
                        onClick = {
                            keyboardController?.hide()
                            if (txtrpemail.isNotEmpty()) {
                                val parseEvents = ParseEvents()
                                val dbresult = parseEvents.ResetUserPassword(txtrpemail)
                                if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {
                                    confirmOpenDialog.value = true
                                } else {
                                    scope.launch {

                                        msg = when (dbresult.errorcode) {
                                            "RUP-103" -> {
                                                context.getString(R.string.emailnotvalid)
                                            }

                                            "RUP-102" -> {
                                                context.getString(R.string.emailnotnull)
                                            }

                                            else -> {
                                                context.getString(R.string.generalexceptionmessage)
                                            }
                                        }
                                        alertbarHostState.showSnackbar(
                                            message = msg,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            } else {
                                scope.launch {
                                    alertbarHostState.showSnackbar(
                                        message = context.getString(R.string.emailnotnull),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                    ) {
                        Text(text = stringResource(id = R.string.reset))
                    }
                }
            }
        }
    )
}


//@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun DeviceForm(
    deviceViewModel: DeviceViewModel = hiltViewModel(),
) {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()

    var progressState = remember { mutableStateOf(false) }
    AppProgress(progressState)


    val deviceformsheetState = rememberBottomSheetScaffoldState(deviceFormSheetState.value)

    var txtmacaddress by rememberSaveable { mutableStateOf("") }
    val ismacaddressVisible by remember { derivedStateOf { txtmacaddress.isNotBlank() } }
    var iserrormacaddress by rememberSaveable { mutableStateOf(false) }

    var txtdevicename by rememberSaveable { mutableStateOf("") }
    val isdevicenameVisible by remember { derivedStateOf { txtdevicename.isNotBlank() } }
    var iserrordevicename by rememberSaveable { mutableStateOf(false) }

    GetLocation(context)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(DeviceTypes.values()[0]) }

    BottomSheetScaffold(scaffoldState = deviceformsheetState,
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 18.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.deviceregistretionformtitle),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                )
                OutlinedTextField(modifier = Modifier
                    .fillMaxWidth()
                    .focusable(true),
                    onValueChange = {
                        txtmacaddress =
                            if (deviceformsheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                                ""
                            } else it
                    },
                    label = { Text(text = stringResource(id = R.string.macaddress)) },
                    value = txtmacaddress,
                    isError = iserrormacaddress,
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    keyboardActions = KeyboardActions {
                        iserrormacaddress = txtmacaddress.isEmpty()
                    },
                    trailingIcon = {
                        if (ismacaddressVisible) {
                            IconButton(onClick = { txtmacaddress = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear, contentDescription = "Clear"
                                )
                            }
                        }
                    })
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        txtdevicename =
                            if (deviceformsheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                                ""
                            } else it
                    },
                    label = { Text(text = stringResource(id = R.string.devicename)) },
                    value = txtdevicename,
                    isError = iserrordevicename,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions {
                        iserrordevicename = txtmacaddress.isEmpty()
                    },
                    trailingIcon = {
                        if (isdevicenameVisible) {
                            IconButton(onClick = { txtdevicename = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear, contentDescription = "Clear"
                                )
                            }
                        }
                    })
                LazyColumn(modifier = Modifier.selectableGroup()) {
                    itemsIndexed(DeviceTypes.values()) { _, item ->
                        var name = ""
                        var iconid = -1
                        when (item.value) {
                            1 -> {
                                name = item.key
                                iconid = R.drawable.t3_icon_48
                            }

                            2 -> {
                                name = item.key
                                iconid = R.drawable.e9_icon_32
                            }
                        }
                        ListItem(
                            modifier = Modifier.selectable(
                                selected = (item == selectedOption),
                                onClick = { onOptionSelected(item) },
                                role = Role.RadioButton
                            ),
                            /*colors = ListItemDefaults.colors(
                                containerColor = Color.Black,
                                headlineColor = Color.White,
                                leadingIconColor = Color.LightGray
                            ),*/
                            headlineContent = { Text(text = name) },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .width(32.dp)
                                        .height(32.dp)
                                ) {
                                    Icon(painterResource(id = iconid), name)
                                }
                            },
                            trailingContent = {
                                RadioButton(
                                    selected = (item == selectedOption),
                                    onClick = null // null recommended for accessibility with screenreaders
                                )
                            }
                        )
                    }
                }
                Text(text = stringResource(id = R.string.deviceregistretionformwarning))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    OutlinedButton(onClick = {
                        txtmacaddress = ""
                        txtdevicename = ""
                        scope.launch {
                            deviceformsheetState.bottomSheetState.partialExpand()
                        }
                    }) {
                        Text(text = context.getString(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = {
                        progressState.value=true
                        if (!(txtmacaddress.isEmpty() || txtdevicename.isEmpty())) {
                            val cMacAddress=appHelpers.formatedMacAddress(txtmacaddress)
                            if (BluetoothAdapter.checkBluetoothAddress(cMacAddress)) {
                                val parseEvents = ParseEvents()
                                val lDevice = Device(
                                    0,
                                    appHelpers.getNOWasSQLDate(),
                                    appHelpers.unformatedMacAddress(txtmacaddress),
                                    txtdevicename,
                                    latLng.latitude.toString(),
                                    latLng.longitude.toString(),
                                    "", null, null, selectedOption.value

                                )
                                val dbresponse = parseEvents.AddEditDevice(lDevice, deviceViewModel)
                                if (dbresponse.eventResultFlags == EventResultFlags.SUCCESS) {
                                    txtmacaddress = ""
                                    txtdevicename = ""
                                    scope.launch {
                                        deviceViewModel.handleViewEvent(
                                            DeviceListViewEvent.AddItem(
                                                lDevice
                                            )
                                        )
                                        deviceformsheetState.bottomSheetState.partialExpand()
                                    }
                                }
                                else{
                                    progressState.value=false
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.err),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                progressState.value=false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.checkmacaddress),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            progressState.value=false
                            if (txtmacaddress.isEmpty()) iserrormacaddress = true
                            if (txtdevicename.isEmpty()) iserrordevicename = true
                        }
                    }) {
                        Text(
                            text = context.getString(R.string.save),
                            style = TextStyle(color = MaterialTheme.colorScheme.primary)
                        )
                    }
                }

            }
        }) {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppProgress(isShow: MutableState<Boolean>) {
    val context = LocalContext.current.applicationContext
    //var openDialog=remember{ mutableStateOf(isShow.value) }
    if (isShow.value) {
        AlertDialog(
            modifier = Modifier.fillMaxSize(),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { isShow.value = false },
            //modifier = Modifier.background(Color.Transparent),
            content = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(100.dp),
                        color = Color.White,
                        strokeWidth = 10.dp
                    )
                    Text(
                        text = context.getString(R.string.pleasewait),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    IvocaboProjectTheme {
        Dashboard()
    }
}*/
