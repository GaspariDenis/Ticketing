package com.example.ticketing.repository

import android.util.Log
import androidx.core.view.accessibility.AccessibilityViewCommand
import androidx.room.Insert
import com.example.ticketing.network.APIService
import com.example.ticketing.network.APIStatus
import com.example.ticketing.vo.Comment
import com.example.ticketing.vo.DataPaged
import com.example.ticketing.vo.extractError
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.listOf

@Singleton
class CommentRepository @Inject constructor(
    val api : APIService
) {
    private val tag = "CommentRepository"

    suspend fun getComments(projectId: String, ticketId: String, page : Int) : APIStatus<DataPaged<Comment>> {
        return try{
            val response = api.getAllComments(projectId, ticketId, page)

            when(response.code()){
                200 -> {
                    Log.d(tag, response.body().toString())
                    APIStatus.Success(response.body() ?: throw Exception("the body was empty."))
                }
                401, 403, 404 -> {
                    Log.e(tag, "code ${response.code()}: ${extractError(response.errorBody())}")
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError( response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error")
            APIStatus.Error(e)
        }
    }

    suspend fun createComment(projectId: String, ticketId: String, comment: Comment) : APIStatus<Comment> {
        return try{
            val response = api.createComment(projectId, ticketId, comment)

            when(response.code()){
                201 -> {
                    APIStatus.Success(response.body() ?: throw Exception())
                }
                400, 401, 403, 404 -> {
                    Log.e(tag, "code ${response.code()}: ${extractError(response.errorBody())}")
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError( response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error")
            APIStatus.Error(e)
        }
    }

    suspend fun deleteComment(projectId: String,ticketId: String, commentId : String) : APIStatus<Unit> {
        return try{
            val response = api.deleteComment(projectId, ticketId, commentId)

            when(response.code()){
                204 -> {
                    Log.d(tag, response.body().toString())
                    APIStatus.Success(Unit)
                }
                401, 403, 404 -> {
                    Log.e(tag, "code ${response.code()}: ${extractError(response.errorBody())}")
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError( response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error")
            APIStatus.Error(e)
        }
    }
}