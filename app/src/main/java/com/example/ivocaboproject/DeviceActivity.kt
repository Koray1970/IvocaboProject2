package com.example.ivocaboproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ivocaboproject.database.localdb.DeviceViewModel
import com.example.ivocaboproject.ui.theme.IvocaboProjectTheme

class DeviceActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IvocaboProjectTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor=Color.Black
                ) {
                    DeviceEvents()
                }
            }
        }
    }
}



@Composable
fun DeviceEvents(deviceViewModel: DeviceViewModel = hiltViewModel()) {
    val context = LocalContext.current.applicationContext
    val activity = LocalContext.current as Activity
    var macaddress=activity.intent.getStringExtra("macaddress").toString()
    val dbdetails=deviceViewModel.getDeviceDetail()

    Text(text = ttt)
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IvocaboProjectTheme {
        Greeting("Android")
    }
}*/
