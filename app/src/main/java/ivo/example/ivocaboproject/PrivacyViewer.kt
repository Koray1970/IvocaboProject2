package ivo.example.ivocaboproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import ivo.example.ivocaboproject.ui.theme.IvocaboProjectTheme
import kotlinx.coroutines.launch
@AndroidEntryPoint
class PrivacyViewer : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ParseUser.getCurrentUser()==null) {
            setContent {
                IvocaboProjectTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Privacy()
                    }
                }
            }
        } else {
            val int = Intent(this, MainActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Privacy() {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(50.dp)
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                text = context.getString(R.string.privacypolicy_title)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, false),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_privacy_tip_24),
                    contentDescription = null,
                    Modifier
                        .fillMaxWidth(.3f)
                        .aspectRatio(1f)
                        .alpha(.6f)
                )
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light,
                text = context.getString(R.string.privacypolicy_detail1)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light,
                text = context.getString(R.string.privacypolicy_detail2)
            )
            Spacer(modifier = Modifier.padding(0.dp, 50.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, false),
            ) {
                Button(onClick = {
                    val int = Intent(context, MainActivity::class.java).apply {
                        context.startActivity(this)
                    }
                }) {
                    Text(context.getString(R.string.ccontinue))
                }
            }
            Spacer(modifier = Modifier.padding(0.dp, 12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, false),
            ) {
                TextButton(onClick = {
                    scope.launch { openDialog.value = true }
                }) {
                    Text(text = context.getString(R.string.privacypolicy_title).uppercase())
                }
            }
        }
    }
    val urlState = rememberWebViewState("https://www.ivocabo.com/gi-zli-li-k-poli-ti-kasi")

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
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
                                onClick = { openDialog.value = false }) {
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


/*@Preview(showBackground = true)
@Composable
fun PrivacyPreview() {
    IvocaboProjectTheme {
        Privacy()
    }
}*/
