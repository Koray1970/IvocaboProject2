package com.example.ivocaboproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ivocaboproject.ui.theme.IvocaboProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IvocaboProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black,
                ) {
                    Dashboard()
                }
            }
        }
    }
}

@Composable
fun Dashboard() {
    RegisterUser()
}

@Composable
fun RegisterUser() {
    Column(modifier = Modifier
        .fillMaxSize().padding(16.dp).background(color = Color.Black)
    ) {

        Image(
            modifier = Modifier
                .width(80.dp)
                .align(alignment = Alignment.CenterHorizontally).padding(0.dp,10.dp),
            painter = painterResource(id = R.drawable.ic_launcher_round),
            contentDescription ="" )

        Text(modifier = Modifier.fillMaxWidth().padding(0.dp,8.dp),
            text = stringResource(id = R.string.rg_title),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                fontSize=24.sp,
                fontWeight = FontWeight.Bold))
        Text(modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.rg_subtitle),
            style = TextStyle(
                color = MaterialTheme.colorScheme.inversePrimary,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center, fontSize =10.sp ))

        var txtrgusername by rememberSaveable { mutableStateOf("") }
        val isusernameVisible by remember { derivedStateOf { txtrgusername.isNotBlank() } }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = { txtrgusername = it },
            label = { Text(text = stringResource(id = R.string.rg_username)) },
            value = txtrgusername,
            textStyle = TextStyle(color = Color.White),
            trailingIcon = {
                if (isusernameVisible) {
                    IconButton(
                        onClick = { txtrgusername = "" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            })
        var txtrgemail by rememberSaveable { mutableStateOf("") }
        val isemailVisible by remember { derivedStateOf { txtrgemail.isNotBlank() } }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = { txtrgemail = it },
            label = { Text(text = stringResource(id = R.string.email)) },
            value = txtrgemail,
            textStyle = TextStyle(color = Color.White),
            trailingIcon = {
                if (isemailVisible) {
                    IconButton(
                        onClick = { txtrgemail = "" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            })
        var txtrgpassword by rememberSaveable { mutableStateOf("") }
        var ispasswordVisible by remember { mutableStateOf(false) }
        val icon = if (ispasswordVisible)
            painterResource(id = R.drawable.baseline_visibility_2480)
        else
            painterResource(id = R.drawable.baseline_visibility_off_24)
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
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
                        painter = icon,
                        contentDescription = "Visibility Icon"
                    )
                }
            })
        Text(
            modifier=Modifier.padding(0.dp,10.dp),
            text = stringResource(id = R.string.rg_warning),
            style = TextStyle(
                color=MaterialTheme.colorScheme.inversePrimary,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )
        )
        Row(){

            FilledTonalButton(onClick = { /*TODO*/ },
                ) {
                Text(text =stringResource(id = R.string.save) )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    IvocaboProjectTheme {
        Dashboard()
    }
}