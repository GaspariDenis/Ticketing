package com.example.ticketing.projectSetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ticketing.R
import com.example.ticketing.dashboard.DashboardScreen
import com.example.ticketing.dashboard.Home
import com.example.ticketing.ui.utils.Alert
import com.example.ticketing.ui.utils.TextField
import com.example.ticketing.vo.Project
import kotlinx.serialization.Serializable

@Serializable
data class ProjectSetting(
    val project: Project
)

@Composable
fun ProjectSettingScreen(
    modifier: Modifier = Modifier,
    viewModel: ProjectSettingViewModel = hiltViewModel(),
    nav : NavController,
    project: Project
) {

    val error by viewModel.errorEvent.collectAsStateWithLifecycle(initialValue = "")

    val success by viewModel.successfulOperation.collectAsStateWithLifecycle(initialValue = false)

    Screen(
        modifier = Modifier.padding(top = 20.dp),
        currentTitle = project.name ?: "Unknown Name",
        currentDescription = project.description ?: "",
        error = error,
        resetError = viewModel::resetError,
        onClickBackArrow = { nav.popBackStack() },
        onClickSave = {title, description ->
            viewModel.uploaProject(project.id ?: "", title, description)
        },
        onClickDelete = {
            viewModel.deleteProject(project.id ?: "")
        },
    )

    LaunchedEffect(success) {
        if(success)
            nav.navigate(Home)
    }
}

@Composable
private fun Screen(
    modifier: Modifier = Modifier,
    currentTitle: String,
    currentDescription : String,
    error: String,
    resetError : () -> Unit,
    onClickBackArrow : () -> Unit,
    onClickSave : (String, String) -> Unit,
    onClickDelete : () -> Unit
) {

    var title by remember { mutableStateOf(currentTitle) }
    var description by remember { mutableStateOf(currentDescription) }

    Scaffold(
        modifier = modifier.padding(start = 16.dp, end = 16.dp),
        topBar = {
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClickBackArrow
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp).padding(end = 4.dp),
                        painter = painterResource(R.drawable.left_arrow),
                        contentDescription = null
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    text = "Impostazioni Progetto"
                )
            }
        },
        bottomBar = {
            Column(Modifier.padding(bottom = 30.dp)) {
                Button(
                    onClick = {
                        onClickSave(title, description)
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x00ffffff)
                    ),
                    enabled = title != "" && description != "",
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush =
                                    if (title != "" && description != "")
                                        Brush.horizontalGradient(
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
                            text = "SALVA"
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onClickDelete,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x00ffffff)
                    ),
                    enabled = true,
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xff845fee),
                                        Color(0xff4e80ee)
                                    )
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
                            text = "ElIMINA PROGETTO"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(top = 16.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                onChange = { str -> title = str},
                labelText = "Nome",
                labelColor = Color(0xff9b86e7),
                placeholder = {
                    Text("Nome progetto")
                }
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                text = description,
                onChange = { str -> description = str},
                labelText = "Descrizione",
                labelColor = Color(0xff9b86e7),
                maxLine = 5,
                singleLine = false,
                placeholder = {
                    Text("Descrizione progetto")
                }
            )
        }
    }

    if(error != "") {
        Alert(
            title = "ERROR",
            message = error,
            onDismiss = {},
            onConfirm = resetError
        )
    }
}