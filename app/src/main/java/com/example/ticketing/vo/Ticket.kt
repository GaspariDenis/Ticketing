package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class Ticket(
    val title : String?,
    val description: String?,
    val status : String?,
    val priority : String?,
    val assigneeId : String?,
)