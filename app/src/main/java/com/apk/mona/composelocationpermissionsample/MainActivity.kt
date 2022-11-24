package com.apk.mona.composelocationpermissionsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.apk.mona.composelocationpermissionsample.ui.theme.ComposeLocationPermissionSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLocationPermissionSampleTheme {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationRequestScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.systemBarsPadding(),
    ) { insetsPadding ->
        LocationRequestPage(
            onClick = {},
            modifier = Modifier.padding(insetsPadding)
        )
    }
}

@Composable
fun LocationRequestPage(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Button(
            onClick = onClick,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                text = "TAP!",
                color = Color.White,
            )
        }
    }
}
