package com.example.ivocaboproject

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ivocaboproject.connectivity.FetchNetworkConnectivity
import com.example.ivocaboproject.connectivity.InternetConnectionStatus
import com.example.ivocaboproject.database.EventResultFlags
import com.example.ivocaboproject.database.ParseEvents
import com.example.ivocaboproject.database.localdb.Device
import com.example.ivocaboproject.database.localdb.DeviceListViewEvent
import com.example.ivocaboproject.database.localdb.DeviceListViewState
import com.example.ivocaboproject.database.localdb.DeviceViewModel
import com.example.ivocaboproject.database.localdb.User
import com.example.ivocaboproject.database.localdb.UserViewModel
import com.example.ivocaboproject.ui.theme.IvocaboProjectTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.ScaleBar
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

val appHelpers = AppHelpers()


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        //context.deleteDatabase("ivocabo.db")


        val cld = FetchNetworkConnectivity(application)
        cld.observe(this) { isConnected ->
            if (isConnected == InternetConnectionStatus.DISCONNECTED) {
                Toast.makeText(
                    context, getString(R.string.internetdisconnected), Toast.LENGTH_LONG
                ).show()
            }
        }

        setContent {
            IvocaboProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black,
                ) {
                    AppNavigator()
                }
            }
        }
    }

    @Composable
    fun AppNavigator() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "dashboard") {
            composable("dashboard") { Dashboard(navController) }
            composable("registeruser") { RegisterUser(navController) }
            composable("DeviceList") { RegisterUser(navController) }
        }
    }
}

lateinit var latLng: LatLng
lateinit var camState: CameraPositionState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Dashboard(
    navController: NavController,
    userviewModel: UserViewModel = hiltViewModel(),
    deviceViewModel: DeviceViewModel = hiltViewModel(),
) {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    if (userviewModel.count <= 0) navController.navigate("registeruser")
    else {
        if (!ParseUser.getCurrentUser().isAuthenticated) {
            val parseEvents = ParseEvents()
            val dbresult = parseEvents.SingInUser(userviewModel)
            if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {

                Toast.makeText(
                    context, context.getString(R.string.userisauthenticated), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION
        )
    )


    latLng = LatLng(0.0, 0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 20f)
    }

    val broadCastLocationMessage = remember { mutableStateOf(latLng) }

    LaunchedEffect(Unit) {
        multiplePermissionState.launchMultiplePermissionRequest()
    }


    Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_START
        context.startService(this)
    }
    val broadcastLocationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        // we will receive data updates in onReceive method.
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
                latLng=LatLng(intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0))
                // on below line we are updating the data in our text view.
                broadCastLocationMessage.value =latLng

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

    val deviceformsheetState = rememberBottomSheetScaffoldState()
    val deviceViewState = deviceViewModel.consumableState().collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    scope.launch {
                        deviceformsheetState.bottomSheetState.expand()
                    }
                }, containerColor = Color(context.getColor(R.color.ic_applogo_background))
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
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

            //device list
            DeviceList(navController, deviceViewState, deviceformsheetState)

        }
    }

    DeviceForm(deviceformsheetState.bottomSheetState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceList(
    navController: NavController,
    state: State<DeviceListViewState>,
    deviceformsheetState: BottomSheetScaffoldState,
    deviceViewModel: DeviceViewModel = hiltViewModel()
) {
    val context = LocalContext.current.applicationContext
    //val deviceformsheetState = rememberBottomSheetScaffoldState()
    val parseEvents = ParseEvents()
    Text(
        text = "Toplam Kayıt : " + state.value.devices.size, style = TextStyle(color = Color.Green)
    )
    val listState = rememberLazyListState()
    val txtitemdelete = stringResource(id = R.string.devicedelete)
    val scope = rememberCoroutineScope()
    LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState, userScrollEnabled = true) {
        itemsIndexed(state.value.devices) { index, item ->
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

            SwipeToDismiss(state = dismissState, background = {
                dismissState.dismissDirection ?: return@SwipeToDismiss
                DeviceSwipeBackground(dismissState = dismissState)
            }, dismissContent = {
                Card(
                    onClick = {
                        val intent = Intent(context, DeviceActivity::class.java)
                        intent.putExtra("macaddress", item.macaddress)
                        context.startActivity(intent)

                    },
                    shape = RoundedCornerShape(0.dp),
                    elevation = CardDefaults.cardElevation(1.dp, 1.dp, 1.dp, 2.dp)
                ) {
                    ListItem(headlineContent = {
                        Text(item.name)
                    }, supportingContent = { Text(item.macaddress) })
                    Divider()
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.LightGray
            DismissValue.DismissedToEnd -> Color.Red//Color.Green
            DismissValue.DismissedToStart -> Color.Red
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

@Composable
fun RegisterUser(
    navController: NavController, userviewModel: UserViewModel = hiltViewModel()
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(color = Color.Black)
        .pointerInput(Unit) {

        }) {

        Image(
            modifier = Modifier
                .width(80.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .padding(0.dp, 10.dp),
            painter = painterResource(id = R.drawable.ic_launcher_round),
            contentDescription = ""
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp),
            text = stringResource(id = R.string.rg_title),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.rg_subtitle),
            style = TextStyle(
                color = MaterialTheme.colorScheme.inversePrimary,
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
            value = txtrgusername,
            textStyle = TextStyle(color = Color.White),
            trailingIcon = {
                if (isusernameVisible) {
                    IconButton(onClick = { txtrgusername = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear, contentDescription = "Clear"
                        )
                    }
                }
            })
        var txtrgemail by rememberSaveable { mutableStateOf("") }
        val isemailVisible by remember { derivedStateOf { txtrgemail.isNotBlank() } }
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            onValueChange = { txtrgemail = it },
            label = { Text(text = stringResource(id = R.string.email)) },
            value = txtrgemail,
            textStyle = TextStyle(color = Color.White),
            trailingIcon = {
                if (isemailVisible) {
                    IconButton(onClick = { txtrgemail = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear, contentDescription = "Clear"
                        )
                    }
                }
            })
        var txtrgpassword by rememberSaveable { mutableStateOf("") }
        var ispasswordVisible by remember { mutableStateOf(false) }
        val icon = if (ispasswordVisible) painterResource(id = R.drawable.baseline_visibility_2480)
        else painterResource(id = R.drawable.baseline_visibility_off_24)
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            onValueChange = { txtrgpassword = it },
            label = { Text(text = stringResource(id = R.string.rg_password)) },
            value = txtrgpassword,
            textStyle = TextStyle(color = Color.White),
            visualTransformation = if (ispasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = {
                    ispasswordVisible = !ispasswordVisible
                }) {
                    Icon(
                        painter = icon, contentDescription = "Visibility Icon"
                    )
                }
            })
        Text(
            modifier = Modifier.padding(0.dp, 10.dp),
            text = stringResource(id = R.string.rg_warning),
            style = TextStyle(
                color = MaterialTheme.colorScheme.inversePrimary,
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
                onClick = {
                    val parseEvents = ParseEvents()
                    val user = User(
                        0,
                        appHelpers.getNOWasSQLDate(),
                        txtrgusername,
                        txtrgemail,
                        txtrgpassword,
                        null
                    )
                    val dbresult = parseEvents.AddUser(user, userviewModel)
                    if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {
                        navController.navigate("dashboard")
                    }
                },
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }

    }
}

//@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun DeviceForm(
    sheetState: SheetState, deviceViewModel: DeviceViewModel = hiltViewModel()
) {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(sheetState)


    var txtmacaddress by rememberSaveable { mutableStateOf("") }
    val ismacaddressVisible by remember { derivedStateOf { txtmacaddress.isNotBlank() } }
    var iserrormacaddress by rememberSaveable { mutableStateOf(false) }

    var txtdevicename by rememberSaveable { mutableStateOf("") }
    val isdevicenameVisible by remember { derivedStateOf { txtdevicename.isNotBlank() } }
    var iserrordevicename by rememberSaveable { mutableStateOf(false) }

    BottomSheetScaffold(scaffoldState = bottomSheetScaffoldState,
        sheetContainerColor = Color.Black,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(text = stringResource(id = R.string.deviceregistretionformtitle))
                OutlinedTextField(modifier = Modifier
                    .fillMaxWidth()
                    .focusable(true),
                    onValueChange = {
                        if (bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                            txtmacaddress = ""
                        } else txtmacaddress = it
                    },
                    label = { Text(text = stringResource(id = R.string.macaddress)) },
                    value = txtmacaddress,
                    textStyle = TextStyle(color = Color.White),
                    isError = iserrormacaddress,
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false, keyboardType = KeyboardType.Number
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
                        if (bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                            txtdevicename = ""
                        } else txtdevicename = it
                    },
                    label = { Text(text = stringResource(id = R.string.devicename)) },
                    value = txtdevicename,
                    textStyle = TextStyle(color = Color.White),
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
                            bottomSheetScaffoldState.bottomSheetState.partialExpand()
                        }
                    }) {
                        Text(text = context.getString(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = {
                        if (!(txtmacaddress.isEmpty() || txtdevicename.isEmpty())) {
                            val parseEvents = ParseEvents()
                            val appHelpers = AppHelpers()
                            val lDevice = Device(
                                0,
                                appHelpers.getNOWasSQLDate(),
                                txtmacaddress,
                                txtdevicename,
                                latLng.latitude.toString(),
                                latLng.longitude.toString(),
                                ""
                            )
                            val dbresponse = parseEvents.AddEditDevice(lDevice, deviceViewModel)
                            if (dbresponse.eventResultFlags == EventResultFlags.SUCCESS) {
                                scope.launch {
                                    deviceViewModel.handleViewEvent(
                                        DeviceListViewEvent.AddItem(
                                            lDevice
                                        )
                                    )
                                    bottomSheetScaffoldState.bottomSheetState.partialExpand()
                                }
                            }
                        } else {
                            if (txtmacaddress.isEmpty()) iserrormacaddress = true
                            if (txtdevicename.isEmpty()) iserrordevicename = true
                        }
                    }) {
                        Text(text = context.getString(R.string.save))
                    }
                }

            }
        }) {

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
