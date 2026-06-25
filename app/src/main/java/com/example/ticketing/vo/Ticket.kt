package com.example.ticketing.vo

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Ticket(
    val id : String? = null,
    val title : String? = null,
    val description: String? = null,
    val status : String? = null,
    val priority : String? = null,
    val assigneeId : String? = null,
    val projectId : String? = null,
    val createdAt : String? = null,
    val createdById: String? = null,
    val deletedAt : String? = null,
    val assignee : User? = null,
    val createdBy : User? = null
){
    fun getTicketStatus() : TicketStatus {
        return when(status){
            "open" -> TicketStatus.open
            "closed" -> TicketStatus.closed
            "progress" -> TicketStatus.in_progress
            else -> TicketStatus.open
        }
    }

    fun getTicketPriority() : PriorityTag {
        return when(priority){
            "high" -> PriorityTag.high
            "medium" -> PriorityTag.medium
            "low" -> PriorityTag.low
            else -> PriorityTag.low
        }
    }
}

fun MagicTicket(projectId : String) = Ticket(
    id = "magicTicket",
    projectId = projectId
)

enum class TicketStatus{
    open,
    in_progress,
    closed
}

enum class PriorityTag {
    high,
    medium,
    low
}

fun getStringFromPriorityTag(tag : PriorityTag) : String {
    return when(tag) {
        PriorityTag.low -> "low"
        PriorityTag.medium -> "medium"
        PriorityTag.high -> "high"
    }
}

val TicketCustomNavType = object : NavType<Ticket>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: Ticket) {
        bundle.putSerializable(key, Json.encodeToString(value))
    }

    override fun get(bundle: Bundle, key: String): Ticket? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Ticket {
        return Json.decodeFromString(android.net.Uri.decode(value))
    }

    override fun serializeAsValue(value: Ticket): String {
        return android.net.Uri.encode(Json.encodeToString(value))
    }
}