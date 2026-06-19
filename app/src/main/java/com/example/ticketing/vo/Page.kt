package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val page : Int,
    val pageSize: Int,
    val totalItems : Int,
    val totalPages : Int
)