package com.example.ticketing.dashboardTickets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ticketing.ui.utils.TextField
import kotlinx.serialization.Serializable

@Serializable
data class DashboardTickets(
    val projectId : String
)

@Composable
fun DashboardTicketsScreen(
    viewModel: TicketsViewModel = hiltViewModel(),
    nav : NavController
){

}

@Composable
fun TicketCard(
    containerColor : Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
        ) {

        }
    }
}