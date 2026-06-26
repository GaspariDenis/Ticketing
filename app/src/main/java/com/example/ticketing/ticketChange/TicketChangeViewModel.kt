package com.example.ticketing.ticketChange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketing.network.APIStatus
import com.example.ticketing.repository.TicketRepository
import com.example.ticketing.vo.PriorityTag
import com.example.ticketing.vo.Ticket
import com.example.ticketing.vo.TicketStatus
import com.example.ticketing.vo.getStringFromPriorityTag
import com.example.ticketing.vo.getStringFromStatusTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TicketChangeViewModel @Inject constructor(
    val repo : TicketRepository
) : ViewModel() {
    private val tag = "TicketChangeViewModel"

    val creationSuccess = MutableStateFlow(false)

    val errorEvent = MutableStateFlow("")

    fun resetErrorEvent() {
        errorEvent.update { "" }
    }

    fun isValid(title: String, description : String, person : String) : Boolean {
        if(title == "")
            return false

        if(description == "")
            return false

        if(person == "")
            return false

        return true
    }

    fun createTicket(projectId : String, title: String, description: String, priorityTag: PriorityTag, userId : String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.createTicket(
                projectId,
                Ticket(
                    title = title,
                    description = description,
                    priority = getStringFromPriorityTag(priorityTag),
                    assigneeId = userId
                )
            )

            when(status) {
                is APIStatus.Success -> { creationSuccess.update { true } }
                is APIStatus.Loading -> {}
                is APIStatus.Error -> { errorEvent.update { status.e.message ?: "Unexpected Error." } }
                is APIStatus.ErrorAPI -> { errorEvent.update { status.errorMessage() } }
            }
        }
    }

    fun updateTicket(
        projectId: String,
        ticketId : String,
        title: String,
        description: String,
        status: TicketStatus,
        priorityTag: PriorityTag,
        userId: String,
        oncSuccess : () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.updateTicket(projectId, ticketId, Ticket(
                title = title,
                description = description,
                status = getStringFromStatusTag(status),
                priority = getStringFromPriorityTag(priorityTag),
                assigneeId = userId
            ) )

            when(status) {
                is APIStatus.ErrorAPI -> { errorEvent.update { status.errorMessage() } }
                is APIStatus.Error -> { errorEvent.update { status.e.message ?: "Unexpected error" } }
                is APIStatus.Loading -> {}
                is APIStatus.Success -> {
                    withContext(Dispatchers.Main) {
                        oncSuccess()
                    }
                }
            }
        }
    }

    fun deleteTicket(projectId: String, ticketId: String, oncSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.deleteTicket(projectId, ticketId)

            when(status) {
                is APIStatus.ErrorAPI -> { errorEvent.update { status.errorMessage() } }
                is APIStatus.Error -> { errorEvent.update { status.e.message ?: "Unexpected error" } }
                is APIStatus.Loading -> {}
                is APIStatus.Success -> {
                    withContext(Dispatchers.Main){
                        oncSuccess()
                    } }
            }
        }
    }
}