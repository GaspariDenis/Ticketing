package com.example.ticketing.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.ticketing.network.APIStatus
import com.example.ticketing.repository.TicketRepository
import com.example.ticketing.vo.Ticket

class TicketPagingSource(
    val backend: TicketRepository,
    val projectId: String
) : PagingSource<Int, Ticket>() {

    private val tag = "PagingSource"

    override suspend fun load(params: LoadParams<Int>) : LoadResult<Int, Ticket> {
        try{
            val nextPageNumber = params.key ?: 1
            val response = backend.getTickets(projectId, nextPageNumber, 20)

            var page : List<Ticket> = listOf()
            when(response){
                is APIStatus.Success -> page = response.data.data
                is APIStatus.Loading -> {}
                is APIStatus.ErrorAPI -> { Log.e(tag, response.errorMessage()) }
                is APIStatus.Error -> {Log.e(tag, response.e.message ?: "Unexpected Error.")}
            }

            return LoadResult.Page(
                data = page,
                prevKey = null,
                nextKey = nextPageNumber + 1
            )
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpecte Error,")
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Ticket>): Int? {
        return state.anchorPosition?.let { ancHorPosition ->
            val anchorPage = state.closestPageToPosition(ancHorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}