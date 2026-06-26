package com.example.ticketing.ticketChange

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ticketing.projectDetails.ProjectDetails
import com.example.ticketing.ui.utils.Alert
import com.example.ticketing.vo.magicTicket
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.Ticket
import com.example.ticketing.vo.UserTag
import kotlinx.serialization.Serializable

@Serializable
data class TicketCreation(
    val members : List<Member>,
    val ticket: Ticket,
    val userTag: UserTag = UserTag.Member
)

@Composable
fun TicketChangeScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketChangeViewModel = hiltViewModel(),
    nav : NavController,
    youTag : UserTag,
    ticket: Ticket,
    members: List<Member>
) {

    val error by viewModel.errorEvent.collectAsStateWithLifecycle(initialValue = "")

    if (ticket.id == magicTicket("").id) {

        val success by viewModel.creationSuccess.collectAsStateWithLifecycle(initialValue = false)

        CreationScreen(
            modifier = modifier,
            memberList = members,
            onClickBackArrow = { nav.popBackStack() },
            onClick = { title, desc, priority, mem ->
                viewModel.createTicket(ticket.projectId!!, title, desc, priority, mem)
            },
            isValid = viewModel::isValid
        )

        LaunchedEffect(success) {
            if (success)
                nav.popBackStack()
        }
    } else {
        ChangeScreen(
            modifier = modifier,
            ticket = ticket,
            memberList = members,
            onClickBackArrow = { nav.popBackStack() },
            onDelete = {
                viewModel.deleteTicket(
                    projectId = ticket.projectId ?: "",
                    ticketId = ticket.id ?: "",
                    oncSuccess = {
                        nav.navigate(ProjectDetails(
                            userTag = youTag,
                            projectId = ticket.projectId ?: throw Exception("No projectId in ticket")
                        ))
                    })
            },
            onClick = { title, message, status, tag, user ->
                viewModel.updateTicket(
                    projectId = ticket.projectId ?: "",
                    ticketId = ticket.id ?: "",
                    title = title,
                    description = message,
                    status = status,
                    priorityTag = tag,
                    userId = user,
                    oncSuccess = { nav.popBackStack() }
                )
            },
            isValid = viewModel::isValid
        )
    }

    if (error != "") {
        Alert(
            title = "ERROR",
            message = error,
            onDismiss = {},
            onConfirm = viewModel::resetErrorEvent
        )
    }
}