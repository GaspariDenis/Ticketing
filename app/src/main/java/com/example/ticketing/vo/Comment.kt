package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id : String? = null,
    val body : String? = null,
    val ticketId : String? = null,
    val userId : String? = null,
    val createAt : String? = null
)