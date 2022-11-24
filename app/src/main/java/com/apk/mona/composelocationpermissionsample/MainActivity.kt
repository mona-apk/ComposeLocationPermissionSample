package com.apk.mona.composelocationpermissionsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.apk.mona.composelocationpermissionsample.ui.theme.ComposeLocationPermissionSampleTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLocationPermissionSampleTheme {
                LocationRequestScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LocationRequestScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locationPermissionState = rememberLocationPermissionState()

    var locationStateText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(locationPermissionState.isLocationGranted, locationPermissionState.shouldShowRationale) {
        val isSystemLocationEnabled = locationPermissionState.isSystemLocationEnabled(context)
        locationStateText = when {
            locationPermissionState.isLocationGranted && isSystemLocationEnabled -> {
                "App and device location permissions are granted."
            }
            locationPermissionState.isLocationGranted -> {
                "Only app location permission is granted."
            }
            locationPermissionState.shouldShowRationale -> {
                "The user should be presented with a rationale."
            }
            else -> {
                "TAP!"
            }
        }
    }

    Scaffold(
        modifier = modifier.systemBarsPadding(),
    ) { insetsPadding ->
        LocationRequestPage(
            locationStateText = locationStateText,
            onClick = {
                locationPermissionState.requestLocationPermission(context = context)
            },
            modifier = Modifier.padding(insetsPadding)
        )
    }
}

@Composable
fun LocationRequestPage(
    locationStateText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = onClick,
            ) {
                Text(
                    text = "TAP!",
                    color = Color.White,
                )
            }
            Text(
                text = locationStateText,
            )
        }
    }
}
