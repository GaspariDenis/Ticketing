package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class DataPaged<T>(
    val data : List<T>,
    val pagination : Page
)
