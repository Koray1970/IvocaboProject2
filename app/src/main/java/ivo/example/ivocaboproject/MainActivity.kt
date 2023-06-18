package ivo.example.ivocaboproject

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
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
import ivo.example.ivocaboproject.bluetooth.IvocaboFetcher
import ivo.example.ivocaboproject.bluetooth.IvocaboleTrackService
import ivo.example.ivocaboproject.connectivity.FetchNetworkConnectivity
import ivo.example.ivocaboproject.connectivity.InternetConnectionStatus
import ivo.example.ivocaboproject.database.EventResultFlags
import ivo.example.ivocaboproject.database.ParseEvents
import ivo.example.ivocaboproject.database.localdb.Device
import ivo.example.ivocaboproject.database.localdb.DeviceListViewEvent
import ivo.example.ivocaboproject.database.localdb.DeviceListViewState
import ivo.example.ivocaboproject.database.localdb.DeviceViewModel
import ivo.example.ivocaboproject.database.localdb.User
import ivo.example.ivocaboproject.database.localdb.UserViewModel
import ivo.example.ivocaboproject.ui.theme.IvocaboProjectTheme
import kotlinx.coroutines.delay
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
            composable("signin") { SignIn(navController) }
            composable("resetpassword") { ResetPassword(navController) }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
private lateinit var deviceformsheetState:BottomSheetScaffoldState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationBar(navController: NavController) {
    val scope = rememberCoroutineScope()
    deviceformsheetState = rememberBottomSheetScaffoldState()
    var context = LocalContext.current.applicationContext
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        Pair(context.getString(R.string.menu_1), Pair("dashboard", R.drawable.baseline_home_24)),
        Pair(context.getString(R.string.menu_2), Pair("", R.drawable.baseline_emoji_people_24)),
        Pair(context.getString(R.string.menu_3), Pair("", R.drawable.baseline_settings_24))
    )
    BottomAppBar(
        actions = {
            items.forEachIndexed { index, s ->
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
                        deviceformsheetState.bottomSheetState.expand()
                    }
                }, containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.addnewdevice),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        })
}

//private lateinit var latLng: LatLng
private lateinit var camState: CameraPositionState
private val gson: Gson = Gson()

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Dashboard(
    navController: NavController,
    userviewModel: UserViewModel = hiltViewModel(),
    deviceViewModel: DeviceViewModel = hiltViewModel(),
) {
    val context = LocalContext.current.applicationContext
    //val application = context.applicationContext as Application
    val scope = rememberCoroutineScope()

    if (userviewModel.count <= 0) {
        navController.navigate("registeruser")
    } else {
        if (!ParseUser.getCurrentUser().isAuthenticated) {
            var user = userviewModel.getUserDetail
            val parseEvents = ParseEvents()
            val dbresult = parseEvents.SingInUser(user.email, user.password, userviewModel)
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
    if (!context.hasLocationPermission()) {
        LaunchedEffect(Unit) {
            multiplePermissionState.launchMultiplePermissionRequest()
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
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        /*floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    scope.launch {
                        deviceformsheetState.bottomSheetState.expand()
                    }
                }, containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.addnewdevice),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,*/

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
                        .height(320.dp),
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
            DeviceList(navController, deviceViewState)
        }
        AppNavigationBar(navController)
    }
    DeviceForm()
}

private lateinit var lIntent: Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceList(
    navController: NavController,
    state: State<DeviceListViewState>,
    deviceViewModel: DeviceViewModel = hiltViewModel(),
) {
    val context = LocalContext.current.applicationContext
    val parseEvents = ParseEvents()
    /*Text(
        text = "Toplam Kayıt : " + state.value.devices.size, style = TextStyle(color = Color.Green)
    )*/
    val listState = rememberLazyListState()
    val txtitemdelete = stringResource(id = R.string.devicedelete)
    val scope = rememberCoroutineScope()
    lIntent = Intent(context, IvocaboleTrackService::class.java)
    val localLifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        deviceViewModel.getTrackDevicelist.observe(localLifecycleOwner) {
            if (!it.isEmpty()) {
                IvocaboleTrackService.devicelist.postValue(it)
            }
        }
        delay(2000L)
        IvocaboleTrackService.SCANNING_STATUS = true
        context.startService(lIntent)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.onSecondaryContainer)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp, 10.dp),
            text = stringResource(id = R.string.devicelisttitle),
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        )
    }

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
                            IvocaboleTrackService.SCANNING_STATUS = false
                            context.stopService(lIntent)
                            delay(1500L)
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
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        leadingContent = {
                            Image(
                                painter = painterResource(id = deviceicon),
                                contentDescription = null
                            )
                        },
                        headlineContent = {
                            Text(
                                item.name,
                                style = TextStyle(fontWeight = FontWeight.Black, fontSize = 24.sp)
                            )
                        },
                        supportingContent = {
                            Text(
                                item.macaddress,
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Light)
                            )
                        })
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

@Composable
fun ViewPrivacyPolicy() {
    Surface() {

    }
}


@Composable
fun RegisterUser(
    navController: NavController, userviewModel: UserViewModel = hiltViewModel(),
) {
    val context = LocalContext.current.applicationContext
    val logodescription = stringResource(id = R.string.logodescription)
    GetLocation(context)
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
            contentDescription = logodescription
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
            supportingText = { Text(text = stringResource(id = R.string.rg_usernamesupporting)) },
            value = txtrgusername,
            textStyle = TextStyle(color = Color.White),
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
            textStyle = TextStyle(color = Color.White),
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
        var displayiconDesc = ""
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
            textStyle = TextStyle(color = Color.White),
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

@Composable
fun SignIn(
    navController: NavController,
    userviewModel: UserViewModel = hiltViewModel(),
    deviceViewModel: DeviceViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    var txtsiusername by rememberSaveable { mutableStateOf("") }
    val issiusernameBlank by remember { derivedStateOf { txtsiusername.isNotBlank() } }

    var txtsipassword by remember { mutableStateOf("") }
    var ispasswordsiVisible by remember { mutableStateOf(false) }
    val icon = if (ispasswordsiVisible) painterResource(id = R.drawable.baseline_visibility_2480)
    else painterResource(id = R.drawable.baseline_visibility_off_24)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color.Black)
    ) {
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
            text = stringResource(id = R.string.signin),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )

        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            onValueChange = { txtsiusername = it },
            label = { Text(text = stringResource(id = R.string.rg_username)) },
            value = txtsiusername,
            textStyle = TextStyle(color = Color.White),
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
            })
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            onValueChange = { txtsipassword = it },
            label = { Text(text = stringResource(id = R.string.rg_password)) },
            value = txtsipassword,
            textStyle = TextStyle(color = Color.White),
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
            })
        Column(
            modifier = Modifier
                .padding(10.dp, 15.dp)
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = stringResource(id = R.string.resetpasswordwarning),
                style = TextStyle(
                    color = Color.White,
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
                        if (txtsiusername.isNotEmpty() && txtsipassword.isNotEmpty()) {
                            val parseEvents = ParseEvents()
                            var dbresult =
                                parseEvents.SingInUser(txtsiusername, txtsipassword, userviewModel)
                            if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {
                                deviceViewModel.syncDeviceList()
                                delay(2800L)
                                navController.navigate("dashboard")
                            }
                        }
                    }
                },
            ) {
                Text(text = stringResource(id = R.string.signin))
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ResetPassword(navController: NavController) {
    val context = LocalContext.current.applicationContext
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
            .padding(16.dp)
            .background(color = Color.Black),
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
                    text = stringResource(id = R.string.resetpasswordformtitle),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    onValueChange = { txtrpemail = it },
                    label = { Text(text = stringResource(id = R.string.email)) },
                    value = txtrpemail,
                    textStyle = TextStyle(color = Color.White),
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
                                var dbresult = parseEvents.ResetUserPassword(txtrpemail)
                                if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {
                                    confirmOpenDialog.value = true
                                } else {
                                    scope.launch {
                                        var msg = ""
                                        when (dbresult.errorcode) {
                                            "RUP-103" -> {
                                                msg = context.getString(R.string.emailnotvalid)
                                            }

                                            "RUP-102" -> {
                                                msg = context.getString(R.string.emailnotnull)
                                            }

                                            else -> {
                                                msg =
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
    //val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(sheetState)


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
                        if (deviceformsheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                            txtmacaddress = ""
                        } else txtmacaddress = it
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
                        if (deviceformsheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                            txtdevicename = ""
                        } else txtdevicename = it
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


                LazyColumn(
                    modifier = Modifier.selectableGroup()

                ) {
                    itemsIndexed(DeviceTypes.values()) { index, item ->
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
                        if (!(txtmacaddress.isEmpty() || txtdevicename.isEmpty())) {
                            val parseEvents = ParseEvents()
                            val lDevice = Device(
                                0,
                                appHelpers.getNOWasSQLDate(),
                                appHelpers.unformatedMacAddress(txtmacaddress),
                                txtdevicename,
                                latLng.latitude.toString(),
                                latLng.longitude.toString(),
                                "", null, true, selectedOption.value

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
                        } else {
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

/*
@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    IvocaboProjectTheme {
        Dashboard()
    }
}*/
