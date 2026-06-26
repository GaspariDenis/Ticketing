package com.example.ticketing.vo

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
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

        val timeZone = ZoneId.systemDefault()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ITALY)
            .withZone(timeZone)

        return formatter.format(instant)
    }
}

val ProjectCustomNavType = object : NavType<Project>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: Project) {
        bundle.putSerializable(key, Json.encodeToString(value))
    }

    override fun get(bundle: Bundle, key: String): Project? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Project {
        return Json.decodeFromString(android.net.Uri.decode(value))
    }

    override fun serializeAsValue(value: Project): String {
        return android.net.Uri.encode(Json.encodeToString(value))
    }
}