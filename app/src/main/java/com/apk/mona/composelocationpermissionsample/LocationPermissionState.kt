package com.apk.mona.composelocationpermissionsample

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberLocationPermissionState(
    multiplePermissionsState: MultiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ),
    ),
    onResult: (ActivityResult) -> Unit = {},
): LocationPermissionState {
    val context = LocalContext.current
    val windowManager = LocalWindowInfo.current

    val locationPermissionState = remember {
        LocationPermissionState(
            context = context,
            multiplePermissionsState = multiplePermissionsState,
        )
    }

    LaunchedEffect(windowManager.isWindowFocused) {
        if (!windowManager.isWindowFocused) return@LaunchedEffect
        locationPermissionState.refreshDeviceLocation()
    }

    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { activityResult ->
        locationPermissionState.refreshDeviceLocation()
        onResult(activityResult)
    }

    DisposableEffect(locationPermissionState, activityResultLauncher) {
        locationPermissionState.activityResultLauncher = activityResultLauncher
        onDispose {
            locationPermissionState.activityResultLauncher = null
        }
    }

    return locationPermissionState
}

@OptIn(ExperimentalPermissionsApi::class)
class LocationPermissionState constructor(
    private val context: Context,
    val multiplePermissionsState: MultiplePermissionsState,
) {
    internal var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>? = null

    fun requestLocationPermission() {
        when {
            isFineLocationGranted() || isCoarseLocationGranted() -> {
                if (isLocationEnabled()) return

                showSystemLocationRequestDialog()
            }
            multiplePermissionsState.shouldShowRationale -> {
                shouldOpenLocationRequestDialog = true
            }
            else -> {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }
        }
    }

    val isLocationGranted by derivedStateOf {
        multiplePermissionsState.permissions.any { permissionState ->
            permissionState.status.isGranted
        } || multiplePermissionsState.revokedPermissions.isEmpty()
    }

    var isDeviceLocationEnabled: Boolean by mutableStateOf(isLocationEnabled())

    internal fun refreshDeviceLocation() {
        isDeviceLocationEnabled = isLocationEnabled()
    }

    val shouldShowRationale
        get() = multiplePermissionsState.shouldShowRationale

    var shouldOpenLocationRequestDialog: Boolean by mutableStateOf(false)

    fun onDismissRequest() {
        shouldOpenLocationRequestDialog = false
    }

    private fun isFineLocationGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isCoarseLocationGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun showSystemLocationRequestDialog() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(
            LocationRequest.Builder(0L).build()
        )

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    activityResultLauncher?.launch(intentSenderRequest) ?: error("ActivityResultLauncher cannot be null")
                } catch (_: IntentSender.SendIntentException) {
                }
            }
        }
    }
}
