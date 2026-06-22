package com.example.ticketing.ui.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Alert(
    modifier: Modifier = Modifier,
    title : String,
    message : String,
    onDismiss: () -> Unit,
    onConfirm : () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        title = {
            Text(title)
        },
        text = {
            Text(message)
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Ok")
            }
        },
        dismissButton = {}
    )
}