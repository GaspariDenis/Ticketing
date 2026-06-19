package com.example.ticketing.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
//Accounts
    @POST("/auth/register")
    suspend fun registerAccount(
        @Body user : RegisterUser
    ) : Response<User>

    @POST("/auth/refresh")
    //Return the access token
    suspend fun getNewTokens(
        @Body refreshToken: String
    ): Response<UserToken>

    @POST("/auth/login")
    suspend fun loginAccount(
        @Body user : User
    ) : Response<UserToken>

    @POST("/auth/logout")
    suspend fun logoutAccount(
        @Body refreshToken: String
    ) : Response<Nothing>

//Projects
    @GET("/projects")
    suspend fun getAllUserProjects(
    ) : Response<List<Project>>

    @POST("/projects")
    suspend fun createProject(
        @Body name : String,
        @Body description : String
    ) : Response<Project>

    @GET("/projects")
    suspend fun getProject(
        @Query("id") id : String
    ) : Response<Project>

    @PUT("/projects")
    suspend fun updateProject(
        @Query("id") id : String,
        @Body name: String,
        @Body description: String
    ) : Response<Project>

    @DELETE("/projects")
    suspend fun deleteProject(
        @Query("id") id: String
    ) : Response<Nothing>

//Members
    @POST("/projects/{id}/members")
    suspend fun addMemberToProject(
        @Path("id") projectId : String,
        @Body member: Member
    ) : Response<Member>

    @DELETE("projects/{id}/members/{userId}")
    suspend fun removeMemberFromProject(
        @Path("id") projectId: String,
        @Path("userId") userId : String
    ) : Response<Nothing>

//Tickets
    @GET("/projects/{id}/tickets")
    suspend fun ticketsOfProject(
        @Path("id") projectId: String,
    ) : Response<DataPaged<Ticket>>

    @POST("/projects/{id}/tickets")
    suspend fun createTickets(
        @Path("id") projectId: String,
        @Body ticket: Ticket
    ) : Response<Nothing>

    @GET("/projects/{id}/tickets/{ticketId}")
    suspend fun ticketDetail(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String
    ) : Response<Nothing>

    @PUT("/projects/{id}/tickets/{ticketId}")
    suspend fun updateTicket(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String,
        @Body ticket: Ticket
    ) : Response<Nothing>

    @DELETE("/projects/{id}/tickets/{ticketId}")
    suspend fun deleteTicket(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String
    ) : Response<Nothing>

//Comments
    @GET("/projects/{id}/tickets/{ticketId}/comments")
    suspend fun getAllComments(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String
    ) : Response<DataPaged<Comment>>

    @POST("/projects/{id}/tickets/{ticketId}/comments")
    suspend fun createComment(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String,
        @Body comment: Comment
    ) : Response<Nothing>

    @DELETE("/projects/{id}/tickets/{ticketId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String,
        @Path("commentId") commentId : String
    ) : Response<Nothing>
}

sealed interface APIStatus<out T> {
    data class Success<T>(val data : T) : APIStatus<T>
    object Loading : APIStatus<Nothing>
    data class ErrorAPI(val error : APIErrors, val code : Int) : APIStatus<Nothing> {
        fun errorMessage() : String {
            if(code == 400)
                return error.errors?.toString() ?: "The format error for the given code was different from the documentation"
            return error.error ?: "The format error for the given code was different from the documentation"
        }
    }
    data class Error(val e : Throwable) : APIStatus<Nothing>
}