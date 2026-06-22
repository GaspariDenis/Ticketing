package com.example.ticketing.dashboard

import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.icu.text.CaseMap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ticketing.R
import com.example.ticketing.projectDetails.ProjectDetails
import com.example.ticketing.ui.utils.Alert
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

    Screen(
        modifier = modifier,
        error = triggerError,
        listOfProjects = list,
        onClickError = viewModel::resetErrorEvent,
        nav = nav
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    error: String,
    listOfProjects : List<Project>,
    nav : NavController,
    onClickError : () -> Unit
) {
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
                        onClick = {},
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icons8_pi__480),
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
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)){
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
        modifier = modifier.padding(top = 14.dp)
            .clickable(onClick = {
                nav.navigate(ProjectDetails(projectId = projectId))
            }),
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Row(
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().weight(1f),
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
fun MemberTag(tag : UserTag) {

    val color = when(tag) {
        UserTag.owner -> Color(0xff50ab7e)
        UserTag.member -> Color(0xff4773d6)
        UserTag.viewer -> Color(0xffaa00ff)
    }

    val text = when(tag){
        UserTag.owner -> "OWNER"
        UserTag.member -> "MEMBER"
        UserTag.viewer -> "VIEWER"
    }

    val containerColor = when(tag) {
        UserTag.owner -> Color(0x5050ab7e)
        UserTag.member -> Color(0x504773d6)
        UserTag.viewer -> Color(0x50aa00ff)
    }

    val borderColor = when(tag) {
        UserTag.owner -> Color(0x6f50ab7e)
        UserTag.member -> Color(0x6f4773d6)
        UserTag.viewer -> Color(0x6faa00ff)
    }

    Card(
        modifier = Modifier.width(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
        border = BorderStroke(1.dp, borderColor),
    ){
        Text(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 2.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = color,
            text = text,
        )
    }
}