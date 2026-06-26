package com.example.ticketing.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.ticketing.network.APIStatus
import com.example.ticketing.repository.CommentRepository
import com.example.ticketing.vo.Comment

class CommentPagingSource(
    val backend: CommentRepository,
    val projectId: String,
    val ticketId: String,
) : PagingSource<Int, Comment>() {

    private val tag = "PagingSource"

    override suspend fun load(params: LoadParams<Int>) : LoadResult<Int, Comment> {
        try{
            val nextPageNumber = params.key ?: 1
            val response = backend.getComments(projectId, ticketId, nextPageNumber)

            var page : List<Comment> = listOf()
            when(response){
                is APIStatus.Success -> page = response.data.data ?: throw Exception("The list was null.")
                is APIStatus.Loading -> {}
                is APIStatus.ErrorAPI -> {
                    Log.e(tag, response.errorMessage())
                    return LoadResult.Error(Exception(response.errorMessage()))
                }
                is APIStatus.Error -> {
                    Log.e(tag, response.e.message ?: "Unexpected Error.")
                    return LoadResult.Error(Exception(response.e.message ?: "Unexpected Error"))
                }
            }

            val nextKey = if(page.isEmpty()) null else nextPageNumber + 1


            return LoadResult.Page(
                data = page,
                prevKey = null,
                nextKey = nextKey
            )
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected Error.")
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition?.let { ancHorPosition ->
            val anchorPage = state.closestPageToPosition(ancHorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}