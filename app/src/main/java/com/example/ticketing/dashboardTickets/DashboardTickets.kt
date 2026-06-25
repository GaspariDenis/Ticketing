package com.example.ticketing.dashboardTickets

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ModalDrawer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.ticketing.R
import com.example.ticketing.ticketChange.TicketCreation
import com.example.ticketing.ticketDetails.TicketDetails
import com.example.ticketing.ui.utils.PriorityTagCard
import com.example.ticketing.vo.MagicTicket
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.Project
import com.example.ticketing.vo.Ticket
import com.example.ticketing.vo.TicketStatus
import com.example.ticketing.vo.UserTag
import kotlinx.serialization.Serializable

@Serializable
data class DashboardTickets(
    val projectId: String,
    val project : Project,
    val youTag : UserTag,
    val projectMembers : List<Member>
)

@Composable
fun DashboardTicketsScreen(
    modifier : Modifier = Modifier,
    viewModel: TicketsViewModel = hiltViewModel(),
    project: Project,
    youTag: UserTag,
    projectMembers: List<Member>,
    nav : NavController
){

    val pagingItem = viewModel.pagingFlow.collectAsLazyPagingItems()

    LaunchedEffect(nav) {
        pagingItem.refresh()
    }

    Screen(
        modifier = modifier,
        pagingItem = pagingItem,
        youTag = youTag,
        onClickBackArrow = { nav.popBackStack() },
        onTicketCreate = { nav.navigate(TicketCreation(
            ticket = MagicTicket(projectId = project.id ?: ""),
            members = project.members ?: listOf()
        )) },
        onClickTicket = {ticketId ->
            nav.navigate(TicketDetails(
                projectId = project.id ?: "",
                ticketId = ticketId,
                youTag = youTag,
                listOfMember = projectMembers
            ))
        }
    )
}

@Composable
private fun Screen(
    modifier : Modifier = Modifier,
    youTag: UserTag,
    pagingItem : LazyPagingItems<Ticket>,
    onClickBackArrow : () -> Unit,
    onTicketCreate: () -> Unit,
    onClickTicket: (String) -> Unit
){
    var create by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp),
        topBar = {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onClickBackArrow
                ){
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 4.dp),
                        painter = painterResource(R.drawable.left_arrow),
                        contentDescription = null
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    text = "Lista Ticket"
                )

                if(youTag != UserTag.viewer){
                    IconButton(
                        modifier = Modifier.size(35.dp),
                        onClick = {create = true},
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icons8_pi__480),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) {innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(
                count = pagingItem.itemCount,
                key = pagingItem.itemKey { it.id!! }
            ){index ->
                val item = pagingItem[index]
                if(item != null){
                    TicketCard(
                        modifier = Modifier.padding(bottom = 8.dp),
                        ticket = item,
                        onClickTicket = onClickTicket
                    )
                }
            }
        }
    }

    LaunchedEffect(create) {
        if(create) {
            onTicketCreate()
        }
    }
}

//Ticket Cart--------------------------------------

val Green = Color(0xFF377258)
val Orange = Color(0xFFE9a23B)
val Purple = Color(0xff845fee)


@Composable
fun TicketCard(
    modifier: Modifier = Modifier,
    ticket: Ticket,
    onClickTicket : (String) -> Unit
) {

    val tag = ticket.getTicketStatus()

    val borderColor = when(tag){
        TicketStatus.open -> Purple
        TicketStatus.in_progress -> Orange
        TicketStatus.closed -> Green
    }

    Card(
        onClick = {
            onClickTicket(ticket.id ?: "")
        },
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = borderColor
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        text = ticket.title ?: "Title Unknown"
                    )

                    PriorityTagCard(
                        tag = ticket.getTicketPriority()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        text = "Assegnato a: ${ticket.assignee?.name}"
                    )

                    Text(
                        color = borderColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        text = when(tag){
                            TicketStatus.open -> "OPEN"
                            TicketStatus.in_progress -> "IN PROGERSS"
                            TicketStatus.closed -> "CLOSED"
                        }
                    )
                }
            }
        }
    }
}

//-------------------------------------------------