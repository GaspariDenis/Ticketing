package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val content : String,
    val commentId : String
)