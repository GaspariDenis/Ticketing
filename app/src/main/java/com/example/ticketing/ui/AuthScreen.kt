package com.example.ticketing.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    nav : NavController,
    viewModel : AuthViewModel = hiltViewModel()
) {
    val triggerError by viewModel.errorEvent.collectAsStateWithLifecycle(initialValue = "")

    viewModel.login()

    Content(
        modifier = modifier,
        errorMessage = triggerError
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    errorMessage : String
) {
    if(errorMessage != "")
    Alert(
        title = "Error",
        message = errorMessage,
        onConfirm = {},
        onDismiss = {}
    )
}

@Composable
fun Alert(
    title : String,
    message : String,
    onDismiss: () -> Unit,
    onConfirm : () -> Unit,
    ) {
    AlertDialog(
        title = {
            Text(title)
        },
        text = {
            Text(message)
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {}
    )
}