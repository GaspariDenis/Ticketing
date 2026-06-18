package com.example.ticketing.network

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUser(
    val name : String,
    val email : String,
    val password : String
)

@Serializable
data class User (
    val email : String,
    val password : String
)

@Serializable
data class UserToken(
    val accessToken : String,
    val refreshToken: String
)

@Serializable
data class Project(
    val id : String,
    val name : String,
    val description : String?
)

@Serializable
data class Member(
    val email: String?,
    val role : String,
    val userId : String?
)

@Serializable
data class DataPaged<T>(
    val data : List<T>,
    val pagination : Page
)

@Serializable
data class Ticket(
    val title : String,
    val description: String,
    val status : String?,
    val priority : String,
    val assigneeId : String,
)

@Serializable
data class Comment(
    val content : String,
    val commentId : String
)

@Serializable
data class Page(
    val page : Int,
    val pageSize: Int,
    val totalItems : Int,
    val totalPages : Int
)