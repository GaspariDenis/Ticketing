package com.example.ticketing.dashboard

import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.icu.text.CaseMap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ticketing.R
import com.example.ticketing.auth.Auth
import com.example.ticketing.projectDetails.ProjectDetails
import com.example.ticketing.ui.utils.Alert
import com.example.ticketing.ui.utils.MemberTag
import com.example.ticketing.ui.utils.TextField
import com.example.ticketing.vo.Project
import com.example.ticketing.vo.User
import com.example.ticketing.vo.UserTag
import com.example.ticketing.vo.UserToken
import kotlinx.serialization.Serializable

@Serializable
object Home

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: dashboardViewModel = hiltViewModel(),
    nav : NavController
) {
    LaunchedEffect(true) {
        viewModel.getAllProject()
    }

    val triggerError by viewModel.errorEvent.collectAsStateWithLifecycle(initialValue = "")

    val list by viewModel.projects.collectAsStateWithLifecycle(initialValue = listOf())

    val logged by viewModel.isLogged.collectAsStateWithLifecycle(initialValue = true)

    LaunchedEffect(logged) {
        if(!logged){
            nav.navigate(Auth)
        }
    }

    Screen(
        modifier = modifier,
        error = triggerError,
        listOfProjects = list,
        nav = nav,
        onClickLogout = viewModel::logoutUser,
        onCreateProject = viewModel::createProject,
        onClickError = viewModel::resetErrorEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    error: String,
    listOfProjects : List<Project>,
    nav : NavController,
    onClickLogout: () -> Unit,
    onCreateProject : (String, String) -> Unit,
    onClickError : () -> Unit
) {

    var create by remember { mutableStateOf(false) }

    var user by remember { mutableStateOf(false) }

    if(create){
        createProject(
            onDismiss = { create = false },
            onClick = { name, description ->
                onCreateProject(name, description)
                create = false
            }
        )
    }

    if(user){
        userDetail(
            onDismiss = {user = false},
            onClickLogout = {
                onClickLogout()
                user = false
            }
        )
    }

    if(error != ""){
        Alert(
            title = "Error",
            message = error,
            onDismiss = {},
            onConfirm = onClickError
        )
    }

    Scaffold(
        modifier = modifier.padding(start = 18.dp, end = 18.dp),
        topBar = {
            Column{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        text = "I tuoi Progetti")

                    IconButton(
                        modifier = Modifier.size(35.dp),
                        onClick = {create = true},
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icons8_pi__480),
                            contentDescription = null
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        modifier = Modifier.size(35.dp),
                        shape = RoundedCornerShape(0),
                        onClick = { user = true },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.profile_round_1342_svgrepo_com),
                            contentDescription = null
                        )
                    }
                }

                //Da configurare!!
                DockedSearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    inputField = {
                        SearchBarDefaults.InputField(
                            modifier = Modifier,
                            query = "",
                            onQueryChange = {},
                            onSearch = {},
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = {
                                Text(
                                    fontSize = 16.sp,
                                    text = "Cerca Progetto...",
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(R.drawable.icons8_search_384),
                                    contentDescription = null
                                )
                            }
                        )
                    },
                    expanded = false,
                    onExpandedChange = {},
                    content = {}
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)){
            items(items = listOfProjects, itemContent = {item ->
                ProjectCard(
                    title = item.name ?: "Unnamed project",
                    description = item.description ?: "No description",
                    userTag = item.role ?: UserTag.viewer,
                    projectId = item.id ?: "",
                    nav = nav
                )
            })
        }
    }
}

@Composable
fun ProjectCard(
    modifier : Modifier = Modifier,
    title: String,
    description: String,
    userTag: UserTag,
    projectId : String,
    nav: NavController
) {
    Card(
        modifier = modifier
            .padding(top = 14.dp)
            .clickable(onClick = {
                nav.navigate(ProjectDetails(
                    projectId = projectId,
                    userTag = userTag))
            }),
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Row(
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    text = title
                )

                MemberTag(userTag)
            }

            Text(
                modifier= Modifier.padding(top = 7.dp, bottom = 10.dp),
                text = description,
            )
        }
    }
}

@Composable
fun createProject(
    onDismiss : () -> Unit,
    onClick : (String, String) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val isValid = name != "" && description != ""

    Dialog(
        onDismissRequest = onDismiss
    ){
        Column {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                text = name,
                onChange = { str -> name = str},
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

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    onClick(name, description)
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 16.dp, end = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x00ffffff)
                ),
                enabled = isValid,
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush =
                                if (isValid) Brush.horizontalGradient(
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
                        text = "CREA PROGETTO"
                    )
                }
            }
        }
    }
}

@Composable
fun userDetail(
    onDismiss: () -> Unit,
    onClickLogout: () -> Unit,
){
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Button(
            onClick = onClickLogout,
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
                    modifier = Modifier,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                    text = "ESCI DALL'ACCOUNT"
                )
            }
        }
    }
}