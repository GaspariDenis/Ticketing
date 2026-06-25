package com.example.ticketing.network

import android.util.Log
import com.example.ticketing.vo.APIErrors
import com.example.ticketing.vo.Comment
import com.example.ticketing.vo.DataPaged
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.Project
import com.example.ticketing.vo.RegisterUser
import com.example.ticketing.vo.Ticket
import com.example.ticketing.vo.UIProject
import com.example.ticketing.vo.User
import com.example.ticketing.vo.UserToken
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
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
        @Body refreshToken: RefreshTokenRequest
    ): Response<UserToken>

    @POST("/auth/login")
    suspend fun loginAccount(
        @Body user : User
    ) : Response<UserToken>

    @POST("/auth/logout")
    suspend fun logoutAccount(
        @Body refreshToken: UserToken
    ) : Response<String>

//Projects
    @GET("/projects")
    suspend fun getAllUserProjects(
    ) : Response<List<Project>>

    @POST("/projects")
    suspend fun createProject(
        @Body project: Project
    ) : Response<Project>

    @GET("/projects/{id}")
    suspend fun getProject(
        @Path("id") id : String
    ) : Response<Project>

    @PUT("/projects/{id}")
    suspend fun updateProject(
        @Path("id") id : String,
        @Body project : UIProject
    ) : Response<Project>

    @DELETE("/projects/{id}")
    suspend fun deleteProject(
        @Path("id") id: String
    ) : Response<String>

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
    ) : Response<String>

    @GET("/projects/{id}/my-role")
    suspend fun getRoleForProject(
        @Path("id") projectId : String
    ) : Response<Member>

//Tickets
    @GET("/projects/{id}/tickets")
    suspend fun ticketsOfProject(
        @Path("id") projectId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize : Int
    ) : Response<DataPaged<Ticket>>

    @POST("/projects/{id}/tickets")
    suspend fun createTickets(
        @Path("id") projectId: String,
        @Body ticket: Ticket
    ) : Response<Ticket>

    @GET("/projects/{id}/tickets/{ticketId}")
    suspend fun ticketDetail(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String
    ) : Response<Ticket>

    @PUT("/projects/{id}/tickets/{ticketId}")
    suspend fun updateTicket(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String,
        @Body ticket: Ticket
    ) : Response<Ticket>

    @DELETE("/projects/{id}/tickets/{ticketId}")
    suspend fun deleteTicket(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String
    ) : Response<String>

//Comments
    @GET("/projects/{id}/tickets/{ticketId}/comments")
    suspend fun getAllComments(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String,
        @Query("page") page : Int
    ) : Response<DataPaged<Comment>>

    @POST("/projects/{id}/tickets/{ticketId}/comments")
    suspend fun createComment(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String,
        @Body comment: Comment
    ) : Response<Comment>

    @DELETE("/projects/{id}/tickets/{ticketId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("id") projectId: String,
        @Path("ticketId") ticketId: String,
        @Path("commentId") commentId : String
    ) : Response<String>
}

sealed interface APIStatus<out T> {
    data class Success<T>(val data : T) : APIStatus<T>
    object Loading : APIStatus<Nothing>
    data class ErrorAPI(val error : APIErrors, val code : Int) : APIStatus<Nothing> {
        fun errorMessage() : String {
            Log.e("ErrorAPI", error.error ?: "")
            if(code == 400)
                return error.errors?.toString() ?: "The format error for the given code($code) was different from the documentation"
            return error.error ?: "The format error for the given code($code) was different from the documentation"
        }
    }
    data class Error(val e : Throwable) : APIStatus<Nothing>
}

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)