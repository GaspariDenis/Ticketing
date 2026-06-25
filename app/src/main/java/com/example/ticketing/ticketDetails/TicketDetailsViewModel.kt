package com.example.ticketing.ticketDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.ticketing.network.APIStatus
import com.example.ticketing.paging.CommentPagingSource
import com.example.ticketing.repository.AuthRepository
import com.example.ticketing.repository.CommentRepository
import com.example.ticketing.repository.TicketRepository
import com.example.ticketing.vo.Comment
import com.example.ticketing.vo.Ticket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TicketDetailsViewModel @Inject constructor(
    val ticketRepo : TicketRepository,
    val commentRepo : CommentRepository,
    val authRepo : AuthRepository,
    val saveStateHandle : SavedStateHandle
) : ViewModel() {

    private val tag = "TicketDetailsViewModel"

    val pagingFlow : Flow<PagingData<Comment>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            CommentPagingSource(
                backend = commentRepo,
                projectId = checkNotNull(saveStateHandle["projectId"]),
                ticketId = checkNotNull(saveStateHandle["ticketId"])
            )
        }
    ).flow.cachedIn(viewModelScope)

    val errorEvent = MutableStateFlow("")

    fun resetErrorEvent() {
        errorEvent.update { "" }
    }

    val ticketDetails = MutableStateFlow(Ticket())

    fun getTicketDetails(projectId : String, ticketId : String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = ticketRepo.getDetailTicket(projectId, ticketId)

            when(status) {
                is APIStatus.Error -> { errorEvent.update { status.e.message ?: "Unexpected Error" } }
                is APIStatus.ErrorAPI -> { errorEvent.update { status.errorMessage() } }
                is APIStatus.Loading -> {}
                is APIStatus.Success -> { ticketDetails.update { status.data } }
            }
        }
    }

    val deletedSuccess = MutableStateFlow(false)

    fun deleteComment(projectId: String, ticketId: String, commentId : String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = commentRepo.deleteComment(projectId, ticketId, commentId)

            when(status) {
                is APIStatus.ErrorAPI -> { errorEvent.update { status.errorMessage() } }
                is APIStatus.Error -> { errorEvent.update { status.e.message ?: "Unknown Error" } }
                is APIStatus.Loading -> {}
                is APIStatus.Success -> { deletedSuccess.update { true } }
            }
        }
    }

    fun createComment(message: String, projectId: String, ticketId: String, onSuccess : () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = commentRepo.createComment(projectId, ticketId, Comment(
                body = message
            ))

            when(status) {
                is APIStatus.ErrorAPI -> { errorEvent.update { status.errorMessage() } }
                is APIStatus.Error -> { errorEvent.update { status.e.message ?: "Unexpected Error" } }
                is APIStatus.Loading -> {}
                is APIStatus.Success -> {onSuccess()}
            }
        }
    }

    val userId = MutableStateFlow("")

    fun getUserId() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = authRepo.getUserDb()
            userId.update { token.id ?: "" }
        }
    }
}