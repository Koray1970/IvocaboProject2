package ivo.example.ivocaboproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ivo.example.ivocaboproject.database.localdb.UserViewModel
import ivo.example.ivocaboproject.ui.theme.IvocaboProjectTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IvocaboProjectTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    contentColor=MaterialTheme.colorScheme.secondaryContainer,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Settings()
                }
            }
        }
    }
}

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(userViewModel: UserViewModel = hiltViewModel()) {
    val context = LocalContext.current.applicationContext
    val user = userViewModel.getUserDetail

    Column(
        Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .verticalScroll(rememberScrollState())) {
        Text(
            modifier= Modifier
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
                    val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
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
                    val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
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
                    val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
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
                    context.getString(R.string.prf_permissions),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            trailingContent = {
                IconButton(onClick = {
                    val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
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
                    val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
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
                    val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
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
                    context.getString(R.string.prf_aboutivocabo),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            trailingContent = {
                IconButton(onClick = {
                    val int = Intent(context, ProfileActivity::class.java).apply {
                        context.startActivity(this)
                    }
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
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    IvocaboProjectTheme {
        Settings()
    }
}