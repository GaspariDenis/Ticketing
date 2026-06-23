package com.example.ticketing.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ticketing.R
import com.example.ticketing.ui.utils.Alert
import com.example.ticketing.ui.utils.TextField
import kotlinx.serialization.Serializable

@Serializable
object Register

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    nav : NavController
){

    val errorMessage by viewModel.errorEvent.collectAsStateWithLifecycle(initialValue = "")

    val createAccount by viewModel.created.collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(createAccount) {
        if(createAccount)
            nav.popBackStack()
    }

    Screen(
        modifier = modifier,
        errorMessage = errorMessage,
        onConfirm = viewModel::resetErrorEvent,
        isValid = viewModel::checkField,
        onClickButton = viewModel::registerUser
    )
}

@Composable
private fun Screen(
    modifier : Modifier = Modifier,
    errorMessage : String,
    onConfirm : () -> Unit,
    isValid : (String, String, String) -> Boolean,
    onClickButton : (String, String, String) -> Unit
) {

    var user by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if(errorMessage != ""){
        Alert(
            modifier = modifier,
            title = "Error",
            message = errorMessage,
            onConfirm = onConfirm,
            onDismiss = {}
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
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

        TextField(
            modifier = modifier,
            text = user,
            labelText = "UserName",
            labelColor = Color(0xff9b86e7),
            digitPassword = false,
            onChange = {str -> user = str },
            placeholder = {
                Text("tu@azienda.com")
            }
        )

        TextField(
            modifier = modifier,
            text = email,
            labelText = "Email",
            labelColor = Color(0xff9b86e7),
            digitPassword = false,
            onChange = {email = it},
            placeholder = {
                Text("tu@azienda.com")
            }
        )

        TextField(
            modifier = modifier,
            text = password,
            labelText = "Password",
            labelColor = Color(0xff9b86e7),
            digitPassword = true,
            onChange = {password = it},
            placeholder = {
                Text(
                    text = "password"
                )
            }
        )

        Button(
            onClick = {
                onClickButton(user, email, password)
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, end = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x00ffffff)
            ),
            enabled = isValid(user, email, password),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            if (isValid(user, email, password)) Brush.horizontalGradient(
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
                    text = "REGISTRATI"
                )
            }
        }
    }
}