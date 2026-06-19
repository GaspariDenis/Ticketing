package com.example.ticketing.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ticketing.Home
import com.example.ticketing.R
import com.example.ticketing.ui.theme.Indaco
import kotlinx.serialization.Serializable


@Serializable
object Auth

@Composable
fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    nav : NavController,
    viewModel : AuthViewModel = hiltViewModel()
) {
    val triggerError by viewModel.errorEvent.collectAsStateWithLifecycle(initialValue = "")

    val logged by viewModel.loginSuccess.collectAsStateWithLifecycle(initialValue = false)

    if(logged)
        nav.navigate(Home)

    Content(
        modifier = modifier,
        errorMessage = triggerError,
        emailText = viewModel::getEmail,
        isValid = viewModel::checkField,
        onConfirm = viewModel::resetErrorEvent,
        onChangeEmailText = viewModel::setEmail,
        passwordText = viewModel::getPassword,
        onChangePasswordText = viewModel::setPassword,
        onClickButton = viewModel::login
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    errorMessage : String,
    onClickButton : () -> Unit,
    isValid : () -> Boolean,
    onConfirm: () -> Unit,
    emailText : () -> String,
    onChangeEmailText : (String) -> Unit,
    passwordText : () -> String,
    onChangePasswordText: (String) -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                text = "Ticketing App"
            )

            Text(
                modifier = Modifier,
                fontSize = 17.sp,
                text = stringResource(R.string.auth_text_credential)
            )
        }

        Text(
            modifier = Modifier,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xff9b86e7),
            text = "Email"
        )

        TextField(
            modifier = modifier,
            text = emailText,
            placeholder = {
                Text("tu@azienda.com")
            },
            onChange = onChangeEmailText
        )

        Text(
            modifier = Modifier,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xff9b86e7),
            text = "Password"
        )

        TextField(
            modifier = modifier,
            text = passwordText,
            placeholder = {
                Text(
                    text = "password"
                )
            },
            onChange = onChangePasswordText
        )

        Button(
            onClick = onClickButton,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, end = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x00ffffff)
            ),
            enabled = isValid(),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            if (isValid()) Brush.horizontalGradient(
                                colors = listOf(Color(0xff845fee), Color(0xff4e80ee))
                            )
                            else
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xffaabef0), Color(0xffceb9fa))
                                )
                    ),
                contentAlignment = Alignment.Center,
            ){
                Text(
                    modifier = Modifier
                    ,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                    text = stringResource(R.string.auth_access)
                )
            }
        }

    }

    if(errorMessage != ""){
        Alert(
            modifier = modifier,
            title = "Error",
            message = errorMessage,
            onConfirm = onConfirm,
            onDismiss = {}
        )
    }
}

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit,
    text : () -> String,
    onChange : (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        placeholder = placeholder,
        value = text(),
        onValueChange = onChange,
        shape = RoundedCornerShape(16.dp),
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indaco,
            unfocusedBorderColor = Color.Gray,
            unfocusedContainerColor = Color(0xff192032),
            focusedContainerColor = Color(0xff192032)
        )
    )
}

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