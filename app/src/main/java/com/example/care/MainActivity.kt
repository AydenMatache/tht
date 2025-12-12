package com.example.care

import android.app.Activity
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.care.ui.theme.CareTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CareTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CareApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CareApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val inspectionViewModel: InspectionViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "start",
        modifier = modifier
    ) {
        composable("start") {
            StartScreen(navController = navController)
        }
        composable("checklist") {
            ChecklistScreen(navController = navController, viewModel = inspectionViewModel)
        }
        composable("summary") {
            SummaryScreen(viewModel = inspectionViewModel)
        }
        composable("domer") {
            DomerScreen()
        }
    }
}

@Composable
fun StartScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to your Pre-Trip Inspection")
        Button(onClick = { navController.navigate("checklist") }) {
            Text("Begin Inspection")
        }
    }
}

@Composable
fun ChecklistScreen(navController: NavController, viewModel: InspectionViewModel) {
    val currentItem = viewModel.inspectionItems[viewModel.currentItemIndex.value]
    val isLastItem = viewModel.currentItemIndex.value == viewModel.inspectionItems.size - 1
    val isDomerItem = currentItem.name == "Domer"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Category: ${currentItem.category}")
        Text("Item: ${currentItem.name}")

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(onClick = { viewModel.onPassClicked() }) {
                Text("Pass")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { 
                if (isDomerItem) {
                    navController.navigate("domer")
                } else {
                    viewModel.onFailClicked()
                }
            }) {
                Text("Fail")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (isLastItem) {
                    navController.navigate("summary")
                } else {
                    viewModel.goToNextItem()
                }
            },
            enabled = currentItem.status != Status.UNCHECKED
        ) {
            Text(if (isLastItem) "Finalize Inspection" else "Next")
        }
    }
}

@Composable
fun SummaryScreen(viewModel: InspectionViewModel) {
    val completionTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Inspection Summary", modifier = Modifier.padding(bottom = 16.dp))
        Text("Completion Time: $completionTime", modifier = Modifier.padding(bottom = 16.dp))
        viewModel.inspectionItems.forEach { item ->
            Text("${item.name}: ${item.status}")
        }
    }
}

@Composable
fun DomerScreen() {
    val context = LocalContext.current
    var countdown by remember { mutableIntStateOf(10) }
    val infiniteTransition = rememberInfiniteTransition(label = "DomerTransition")
    val backgroundColor by infiniteTransition.animateColor(
        initialValue = Color.Red,
        targetValue = Color.Black,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DomerColorAnimation"
    )

    val ringtone = remember {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        RingtoneManager.getRingtone(context, alarmUri)
    }

    LaunchedEffect(ringtone) {
        ringtone?.play()

        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        (context as? Activity)?.finish()
    }

    DisposableEffect(ringtone) {
        onDispose {
            ringtone?.stop()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.nuclear), contentDescription = "Nuclear Symbol")
        Text("Countdown: $countdown", color = Color.White, fontSize = 32.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    CareTheme {
        StartScreen(navController = rememberNavController())
    }
}
