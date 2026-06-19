package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val email: String?,
    val role : String?,
    val userId : String?
)