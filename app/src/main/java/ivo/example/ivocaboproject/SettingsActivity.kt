package ivo.example.ivocaboproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.parse.ParseUser
import ivo.example.ivocaboproject.database.EventResultFlags
import ivo.example.ivocaboproject.database.ParseEvents
import ivo.example.ivocaboproject.database.localdb.UserViewModel
import ivo.example.ivocaboproject.ui.theme.IvocaboProjectTheme
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IvocaboProjectTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    contentColor = MaterialTheme.colorScheme.secondaryContainer,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Settings()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
lateinit var profileSheetScaffoldState: BottomSheetScaffoldState

@OptIn(ExperimentalMaterial3Api::class)
lateinit var permissionsSheetScaffoldState: BottomSheetScaffoldState

var privacyOpenDialog= mutableStateOf(false)

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(userViewModel: UserViewModel = hiltViewModel()) {
    var scope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext
    val user = userViewModel.getUserDetail
    profileSheetScaffoldState = rememberBottomSheetScaffoldState(SheetState(false))
    permissionsSheetScaffoldState = rememberBottomSheetScaffoldState(SheetState(false))
    Column(
        Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            text = context.getString(R.string.prf_settings_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        //start::profile
        Text(
            text = context.getString(R.string.prf_settings_title).uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(15.dp, 35.dp, 0.dp, 10.dp)
        )
        Divider()
        ListItem(
            headlineContent = { Text(user.username) },
            leadingContent = {
                Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    val initials = (user.username.take(1)).uppercase()
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(SolidColor(Color.DarkGray))
                    }
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            },
            trailingContent = {
                IconButton(onClick = {
                    scope.launch {
                        profileSheetScaffoldState.bottomSheetState.expand()
                    }
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
        )
        Divider()
        //end::profile
        //start::your feed back
        Text(
            text = context.getString(R.string.prf_yourfeedback_title).uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(15.dp, 35.dp, 0.dp, 10.dp)
        )
        Divider()
        ListItem(
            headlineContent = {
                Text(
                    context.getString(R.string.prf_sendfeedback),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            trailingContent = {
                IconButton(onClick = {
                    /*val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
                    }*/
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
        )
        Divider()
        //end::your feed back
        //start::default settings
        Text(
            text = context.getString(R.string.prf_defaultsettings_title).uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(15.dp, 35.dp, 0.dp, 10.dp)
        )
        Divider()
        ListItem(
            headlineContent = {
                Text(
                    context.getString(R.string.prf_savedlocations),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            trailingContent = {
                IconButton(onClick = {
                    /*val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
                    }*/
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
        )
        Divider()
        ListItem(
            headlineContent = {
                Text(
                    context.getString(R.string.prf_permissions),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            trailingContent = {
                IconButton(onClick = {
                    scope.launch {
                        permissionsSheetScaffoldState.bottomSheetState.expand()
                    }
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
        )
        Divider()
        ListItem(
            headlineContent = {
                Text(
                    context.getString(R.string.prf_privacy),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            trailingContent = {
                IconButton(onClick = {
                    privacyOpenDialog.value=true
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
        )
        Divider()
        //end::default settings
        //start::more information
        Text(
            text = context.getString(R.string.prf_moreinformation_title).uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(15.dp, 35.dp, 0.dp, 10.dp)
        )
        Divider()
        ListItem(
            headlineContent = {
                Text(
                    context.getString(R.string.prf_help),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            trailingContent = {
                IconButton(onClick = {
                    /*val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
                    }*/
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
        )
        Divider()
        ListItem(
            headlineContent = {
                Text(
                    context.getString(R.string.prf_aboutivocabo),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            trailingContent = {
                IconButton(onClick = {
                    /*val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
                    }*/
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
        )
        Divider(thickness = 1.dp)
    }
    Profile()
    Permissions()
    ShowPrivacy()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(userViewModel: UserViewModel = hiltViewModel()) {
    val parseEvents = ParseEvents()
    val context = LocalContext.current.applicationContext
    var user = userViewModel.getUserDetail
    BottomSheetScaffold(
        modifier = Modifier.padding(15.dp),
        scaffoldState = profileSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = context.getString(R.string.prf_profile_title).uppercase(),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                )

                Divider()
                Column(
                    Modifier.padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = context.getString(R.string.rg_username) + " : " + user.username)
                    Text(text = context.getString(R.string.email) + " : " + user.email)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(0.dp, 20.dp)
                    ) {
                        Button(onClick = {
                            context.deleteDatabase("ivocabo.db")
                            ParseUser.logOut()
                            val int = Intent(context, PrivacyViewer::class.java).apply {
                                context.startActivity(this)
                            }
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_logout_24),
                                    contentDescription = null
                                )
                                Text(text = context.getString(R.string.logout))
                            }
                        }
                        Spacer(modifier = Modifier.weight(2f))
                        Button(onClick = {
                            var dbresult = parseEvents.RemoveUser()
                            if (dbresult.eventResultFlags == EventResultFlags.SUCCESS) {
                                context.deleteDatabase("ivocabo.db")
                                val int = Intent(context, PrivacyViewer::class.java).apply {
                                    context.startActivity(this)
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.generalexceptionmessage),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                                    contentDescription = null
                                )
                                Text(text = context.getString(R.string.remove))
                            }
                        }
                    }
                }

            }
        }) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowPrivacy(){
    val context = LocalContext.current.applicationContext
    val urlState = rememberWebViewState("https://www.ivocabo.com/gi-zli-li-k-poli-ti-kasi")
    if (privacyOpenDialog.value) {
        AlertDialog(
            onDismissRequest = { privacyOpenDialog.value = false },
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            content = {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    context.getString(R.string.privacypolicy_title),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    },
                    bottomBar = {
                        BottomAppBar() {
                            TextButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { privacyOpenDialog.value = false }) {
                                Text(
                                    text = context.getString(R.string.readandconfirm),
                                    style = TextStyle(textAlign = TextAlign.Center)
                                )
                            }
                        }
                    }
                ) { it ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        WebView(
                            urlState
                        )
                    }

                }

            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Permissions(userView: UserViewModel = hiltViewModel()) {
    val context = LocalContext.current.applicationContext
    var user=userView.getUserDetail

    var locationCheckedState = remember { mutableStateOf(false) }
    if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        locationCheckedState.value = true
    }


    var bluetoothCheckedState = remember { mutableStateOf(false) }
    if (Build.VERSION.SDK_INT <= 30) {
        if (context.checkSelfPermission(android.Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
            && context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            bluetoothCheckedState.value = true
        }
    } else {
        if (context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
            && context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED
            && context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            && context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            bluetoothCheckedState.value = true
        }
    }
    var notificationCheckedState = remember { mutableStateOf(false) }
    if (Build.VERSION.SDK_INT > 32) {
        if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationCheckedState.value = true
        }
    } else {
        if (user.notification != null)
            notificationCheckedState.value = userView.getUserDetail.notification!!
    }
    var trackingCheckedState = remember { mutableStateOf(false) }
    if (Build.VERSION.SDK_INT <= 28) {
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            trackingCheckedState.value = true
        }
    } else {
        if (
            context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && context.checkSelfPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            trackingCheckedState.value = true
        }
    }
    var culture= Locale.getDefault().language

    BottomSheetScaffold(
        scaffoldState = permissionsSheetScaffoldState,
        sheetContent = {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text =
                    if(culture==Locale.forLanguageTag("tr-TR").language)
                        context.getString(R.string.prf_permissions)
                        .uppercase(Locale.forLanguageTag("tr-TR"))
                    else
                        context.getString(R.string.prf_permissions).uppercase()
                    ,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                )
                Divider()
                ListItem(
                    supportingContent = {
                        Column() {
                            Text(text=context.getString(R.string.prf_location_warning_title), fontSize = 10.sp, fontWeight = FontWeight.Normal)
                            Text(text=context.getString(R.string.prf_location_warning), fontSize = 10.sp, fontWeight = FontWeight.ExtraLight, lineHeight = 10.sp)
                        }
                    },
                    headlineContent = {
                        Text(
                            context.getString(R.string.prf_prm_location),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    trailingContent = {
                        Checkbox(
                            enabled=false,
                            checked = locationCheckedState.value,
                            onCheckedChange = { locationCheckedState.value = it }
                        )
                    }
                )
                Divider()
                ListItem(
                    supportingContent = {
                        Column() {
                            Text(text=context.getString(R.string.prf_bluetooth_warning_title), fontSize = 10.sp, fontWeight = FontWeight.Normal)
                            Text(text=context.getString(R.string.prf_bluetooth_warning), fontSize = 10.sp, fontWeight = FontWeight.ExtraLight, lineHeight = 10.sp)
                        }
                    },
                    headlineContent = {
                        Text(
                            context.getString(R.string.prf_prm_bluetooth),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    trailingContent = {
                        Checkbox(
                            enabled=false,
                            checked = bluetoothCheckedState.value,
                            onCheckedChange = { bluetoothCheckedState.value = it }
                        )
                    }
                )
                Divider()
                ListItem(
                    supportingContent = {
                        Column() {
                            Text(text=context.getString(R.string.prf_notification_warning_title), fontSize = 10.sp, fontWeight = FontWeight.Normal)
                            Text(text=context.getString(R.string.prf_notification_warning), fontSize = 10.sp, fontWeight = FontWeight.ExtraLight, lineHeight = 10.sp)
                        }
                    },
                    headlineContent = {
                        Text(
                            context.getString(R.string.prf_prm_notification),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    trailingContent = {
                        Checkbox(
                            checked = notificationCheckedState.value,
                            onCheckedChange = { notificationCheckedState.value = it
                                user.notification = it
                                if (it == false)
                                    userView.user.notification = null
                                userView.updateUser(user)
                            }
                        )
                    }
                )
                Divider()
                ListItem(
                    supportingContent = {
                        Column() {
                            Text(text=context.getString(R.string.prf_tracking_warning_title), fontSize = 10.sp, fontWeight = FontWeight.Normal)
                            Text(text=context.getString(R.string.prf_tracking_warning), fontSize = 10.sp, fontWeight = FontWeight.ExtraLight, lineHeight = 10.sp)
                        }
                    },
                    headlineContent = {
                        Text(
                            context.getString(R.string.prf_prm_tracking),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    trailingContent = {
                        Checkbox(
                            enabled=false,
                            checked = trackingCheckedState.value,
                            onCheckedChange = {
                                trackingCheckedState.value = it
                            }
                        )
                    }
                )
                Divider()
            }
        }) {}
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    IvocaboProjectTheme {
        Settings()
    }
}