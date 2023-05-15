package com.example.ivocaboproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.ivocaboproject.database.localdb.Device
import com.example.ivocaboproject.database.localdb.DeviceViewModel
import com.example.ivocaboproject.ui.theme.IvocaboProjectTheme
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
                        text={Text(text=getString(R.string.deviceeventalerttext))},
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
                            title = { Text(text = dbdetails.name) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    GoBackEvent()
                                }) {
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
}


@Composable
fun DeviceEvents(device: Device) {
    val context = LocalContext.current.applicationContext
    val activity = LocalContext.current as Activity
    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {

        
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
