package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUser(
    val name : String?,
    val email : String?,
    val password : String?
)

@Serializable
data class User (
    val id : String?,
    val email : String?,
    val password : String?
)

@Serializable
data class UserToken(
    val accessToken : String?,
    val refreshToken: String?
)