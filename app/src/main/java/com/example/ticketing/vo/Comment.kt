package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id : String?,
    val body : String?,
    val ticketId : String?,
    val userId : String?,
    val createAt : String?
)