package com.example.ticketing.repository

import android.util.Log
import com.example.ticketing.network.APIService
import com.example.ticketing.network.APIStatus
import com.example.ticketing.vo.DataPaged
import com.example.ticketing.vo.RegisterUser
import com.example.ticketing.vo.Ticket
import com.example.ticketing.vo.User
import com.example.ticketing.vo.extractError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepository @Inject constructor(
    val api : APIService
) {
    private val tag = "TicketRepository"

    suspend fun getTickets(projectId: String, page : Int, pageSize : Int) : APIStatus<DataPaged<Ticket>> {
        return try {
            val response = api.ticketsOfProject(projectId, page, pageSize)

            return when(response.code()) {
                200 -> {
                    val body = response.body() ?: throw Exception("the body was empty.")
                    APIStatus.Success(body)
                }

                401, 403, 404 -> {
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError(response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error.")
            APIStatus.Error(e)
        }
    }
}