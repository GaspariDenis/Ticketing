package com.example.ticketing.dashboardTickets

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.ticketing.paging.TicketPagingSource
import com.example.ticketing.repository.TicketRepository
import com.example.ticketing.vo.Project
import com.example.ticketing.vo.Ticket
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class TicketsViewModel @Inject constructor(
    val repo : TicketRepository,
    val saveStateHandle : SavedStateHandle
) : ViewModel() {
    private val tag = "TicketsViewModel"

    val pagingFlow : Flow<PagingData<Ticket>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            TicketPagingSource(
                backend = repo,
                projectId = checkNotNull(saveStateHandle["projectId"]))
        }
    ).flow.cachedIn(viewModelScope)
}