package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id : String?,
    val name : String?,
    val description : String?,
    var role : UserTag?
)
