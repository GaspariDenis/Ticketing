package com.example.ticketing.vo

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Serializable
data class Project(
    val id : String? = null,
    val name : String? = null,
    val description : String? = null,
    var role : UserTag? = null,
    val createdAt : String? = null,
    val members : List<Member>? = null,
){

    fun getFormattedDate() : String {
        if(createdAt.isNullOrEmpty())
            return ""

        val instant = Instant.parse(createdAt)

        val fusoOrario = ZoneId.systemDefault()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ITALY)
            .withZone(fusoOrario)

        return formatter.format(instant)
    }
}
