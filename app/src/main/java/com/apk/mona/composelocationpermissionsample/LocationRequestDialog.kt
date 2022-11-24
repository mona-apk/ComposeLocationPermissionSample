package com.apk.mona.composelocationpermissionsample

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun LocationRequestDialog(
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Allow location permission")
        },
        text = {
            Text(
                "Please allow location permission."
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick,
            ) {
                Text("OK")
            }
        },
        dismissButton = null
    )
}
