package com.example.ticketing.ticketChange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ticketing.network.APIStatus
import com.example.ticketing.repository.TicketRepository
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.PriorityTag
import com.example.ticketing.vo.Ticket
import com.example.ticketing.vo.getStringFromPriorityTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    fun isValid(title: String, description : String, person : Member) : Boolean {
        if(title == "")
            return false

        if(description == "")
            return false

        if(person.userId.isNullOrEmpty())
            return false

        return true
    }

    fun createTicket(projectId : String, title: String, description: String, priorityTag: PriorityTag, member: Member) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.createTicket(
                projectId,
                Ticket(
                    title = title,
                    description = description,
                    priority = getStringFromPriorityTag(priorityTag),
                    assigneeId = member.userId
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
}