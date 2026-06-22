package com.example.ticketing.projectDetails

import android.annotation.SuppressLint
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import kotlinx.serialization.Serializable

@Serializable
data class ProjectDetails(
    val projectId : String
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProjectDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: ProjectViewModel = hiltViewModel(),
    nav : NavController,
    projectId: String
){
    Scaffold(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp),
        topBar = {
            Row(
                modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Dettaglio Progetto"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) { }
    }
}