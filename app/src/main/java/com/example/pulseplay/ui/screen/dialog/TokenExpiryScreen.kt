package com.example.pulseplay.ui.screen.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogProperties

@Composable
fun TokenExpiredDialogScreen() {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismiss on outside touch */ },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    // Optionally, close the activity
                    // (LocalContext.current as? Activity)?.finish()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                }) {
                    Text("No")
                }
            },
            title = { Text("Exit Confirmation") },
            text = { Text("Are you sure you want to exit?") },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}