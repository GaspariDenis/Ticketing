package com.example.ticketing.network

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
    ) : User

    @POST("/auth/refresh")
    //Return the access token
    suspend fun getNewTokens(
        @Body refreshToken: String
    ): UserToken

    @POST("/auth/login")
    suspend fun loginAccount(
        @Body user : User
    ) : UserToken

    @POST("/auth/logout")
    suspend fun logoutAccount(@Body refreshToken: String)

//Projects
    @GET("/projects")
    suspend fun getAllUserProjects() : List<Project>

    @POST("/projects")
    suspend fun createProject(
        @Body name : String,
        @Body description : String
    ) : Project

    @GET("/projects")
    suspend fun getProject(
        @Query("id") id : String
    ) : Project

    @PUT("/projects")
    suspend fun updateProject(
        @Query("id") id : String,
        @Body name: String,
        @Body description: String
    ) : Project

    @DELETE("/projects")
    suspend fun deleteProject(
        @Query("id") id: String
    )

//Members
    @POST("/projects/{id}/members")
    suspend fun addMemberToProject(
        @Path("id") projectId : String,
        @Body member: Member
    ): Member

    @DELETE("projects/{id}/members/{userId}")
    suspend fun removeMemberFromProject(
        @Path("id") projectId: String,
        @Path("userId") userId : String
    )

//Tickets
    @GET("/projects/{id}/tickets")
    suspend fun ticketsOfProject(
        @Path("id") projectId: String,
    ) : DataPaged<Ticket>

    @POST("/projects/{id}/tickets")
    suspend fun createTickets(
        @Path("id") projectId: String,
        @Body ticket: Ticket
    )

    @GET("/projects/{id}/tickets/{ticketId}")
    suspend fun ticketDetail(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String
    )

    @PUT("/projects/{id}/tickets/{ticketId}")
    suspend fun updateTicket(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String,
        @Body ticket: Ticket
    )

    @DELETE("/projects/{id}/tickets/{ticketId}")
    suspend fun deleteTicket(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String
    )

//Comments
    @GET("/projects/{id}/tickets/{ticketId}/comments")
    suspend fun getAllComments(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String
    ) : DataPaged<Comment>

    @POST("/projects/{id}/tickets/{ticketId}/comments")
    suspend fun createComment(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String,
        @Body comment: Comment
    )

    @DELETE("/projects/{id}/tickets/{ticketId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String,
        @Path("commentId") commentId : String
    )
}