package com.example.ticketing.ticketDetails

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DefaultMonotonicFrameClock
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.room.util.TableInfo
import com.example.ticketing.R
import com.example.ticketing.dashboard.createProject
import com.example.ticketing.dashboardTickets.Green
import com.example.ticketing.dashboardTickets.Orange
import com.example.ticketing.dashboardTickets.Purple
import com.example.ticketing.dashboardTickets.TicketCard
import com.example.ticketing.projectDetails.CardMember
import com.example.ticketing.ticketChange.TicketCreation
import com.example.ticketing.ui.utils.Alert
import com.example.ticketing.ui.utils.PriorityTagCard
import com.example.ticketing.ui.utils.TextField
import com.example.ticketing.vo.Comment
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.Ticket
import com.example.ticketing.vo.TicketStatus
import com.example.ticketing.vo.UserTag
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

@Serializable
data class TicketDetails (
    val projectId : String,
    val ticketId : String,
    val youTag: UserTag,
    val listOfMember : List<Member>
)

@Composable
fun TicketDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketDetailsViewModel = hiltViewModel(),
    nav : NavController,
    youTag : UserTag,
    projectMembers : List<Member>,
    projectId: String,
    ticketId: String
) {
    val ticket by viewModel.ticketDetails.collectAsStateWithLifecycle(initialValue = Ticket())

    val pagingItem = viewModel.pagingFlow.collectAsLazyPagingItems()

    val error by viewModel.errorEvent.collectAsStateWithLifecycle(initialValue = "")

    val deleted by viewModel.deletedSuccess.collectAsStateWithLifecycle(initialValue = false)

    val userId by viewModel.userId.collectAsStateWithLifecycle(initialValue = "")

    LaunchedEffect(nav) {
        viewModel.getTicketDetails(projectId, ticketId)
        viewModel.getUserId()
    }

    LaunchedEffect(deleted) {
        if(deleted)
            pagingItem.refresh()
    }

    if(deleted){
        Alert(
            title = "Notifica",
            message = "Eliminato il commento",
            onDismiss = {},
            onConfirm = { viewModel.deletedSuccess.update { false } }
        )
    }

    if(error != ""){
        Alert(
            title = "ERROR",
            message = error,
            onDismiss = {},
            onConfirm = viewModel::resetErrorEvent
        )
    }

    Screen(
        modifier = modifier,
        ticket = ticket,
        userTag = youTag,
        userId = userId,
        paging = pagingItem,
        createComment = {str ->
            viewModel.createComment(str, projectId, ticketId, pagingItem::refresh)
        },
        onClickBackArrow = { nav.popBackStack() },
        onClickEdit = { nav.navigate(TicketCreation(projectMembers, ticket, youTag)) },
        onClickDelete = {comment ->
            viewModel.deleteComment(projectId, ticketId, comment)
        }
    )
}

@Composable
private fun Screen(
    modifier: Modifier = Modifier,
    ticket: Ticket,
    userTag: UserTag,
    userId: String,
    paging : LazyPagingItems<Comment>,
    onClickBackArrow : () -> Unit,
    onClickEdit : () -> Unit,
    createComment : (String) -> Unit,
    onClickDelete : (String) -> Unit
){
    var createComment by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp),
        topBar = {
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClickBackArrow
                ){
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
                    text = "Dettaglio Ticket"
                )

                if(userTag != UserTag.viewer){
                    IconButton(
                        onClick = onClickEdit
                    ){
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(R.drawable.edit),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) {padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            val borderColor = when(ticket.getTicketStatus()){
                TicketStatus.open -> Purple
                TicketStatus.in_progress -> Orange
                TicketStatus.closed -> Green
            }

            Card(
                modifier = Modifier.padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = borderColor
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp).fillMaxWidth().weight(1f),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xffa38cf3),
                            text = ticket.title ?: "Unknown Title"
                        )

                        PriorityTagCard(
                            modifier = Modifier.padding(end = 8.dp),
                            tag = ticket.getTicketPriority(),
                            isClicked = false
                        )
                    }

                    Text(
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                        fontSize = 16.sp,
                        text = "Creato il ${ticket.getFormattedDate()}"
                    )
                }
            }

            Card(
                modifier = Modifier.padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = borderColor
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
                ){
                    Text(
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                        fontSize = 16.sp,
                        text = ticket.description ?: "No description"
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).fillMaxWidth().weight(1f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    text = "Commenti (${paging.itemCount})"
                )

                if(userTag != UserTag.viewer){
                    IconButton(
                        modifier = Modifier.size(35.dp).padding(start = 8.dp),
                        onClick = { createComment = true }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icons8_pi__480),
                            contentDescription = null
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                items(
                    count = paging.itemCount,
                    key = paging.itemKey { it.id!! }
                ){index ->
                    val item = paging[index]
                    if(item != null){
                        CommentCard(
                            modifier = Modifier.padding(bottom = 8.dp),
                            comment = item,
                            userTag = userTag,
                            userId = userId,
                            onClickDelete = onClickDelete
                        )
                    }
                }
            }
        }
    }

    if(createComment){
        CreateComment(
            onDismiss = {},
            onClick = {str ->
                createComment(str)
                createComment = false
            }
        )
    }
}

@Composable
private fun CommentCard(
    modifier: Modifier = Modifier,
    comment: Comment,
    userTag: UserTag,
    userId : String,
    onClickDelete : (String) -> Unit
){
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.Top,
        ){
            Text(
                modifier = Modifier.padding(8.dp).fillMaxWidth().weight(1f),
                text = comment.body ?: "Unknown Text"
            )

            if(userId == comment.userId || userTag == UserTag.owner){
                IconButton(
                    modifier = Modifier.size(35.dp).padding(start = 8.dp),
                    onClick = {
                        onClickDelete(comment.id ?: "")
                    }
                ) {
                    Image(
                        painter = painterResource(R.drawable.trash_icon),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateComment(
    onDismiss : () -> Unit,
    onClick: (String) -> Unit
){
    var message by remember { mutableStateOf("") }

    Dialog(
      onDismissRequest = onDismiss
    ){
        Column() {
            TextField(
                singleLine = false,
                maxLine = 5,
                text = message,
                onChange = {str -> message = str},
                placeholder = {}
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onClick(message)
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 16.dp, end = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x00ffffff)
                ),
                enabled = message != "",
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush =
                                if (message != "") Brush.horizontalGradient(
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
                        text = "INVIA COMMENTO"
                    )
                }
            }
        }
    }
}