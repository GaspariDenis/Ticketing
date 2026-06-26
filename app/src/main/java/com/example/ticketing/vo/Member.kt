package com.example.ticketing.vo

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Member(
    val email: String? = null,
    val role : String? = null,
    val userId : String? = null,
    val user : User? = null
){

    fun getRole() : UserTag {
        return when(role?.lowercase()){
            "owner" -> UserTag.Owner
            "member" -> UserTag.Member
            "viewer" -> UserTag.Viewer
            else -> throw Exception("role not fount.")
        }
    }

}

enum class UserTag {
    Owner,
    Member,
    Viewer
}

fun stringFromTag(tag: UserTag) : String{
    return when(tag){
        UserTag.Owner -> "owner"
        UserTag.Member -> "member"
        UserTag.Viewer -> "viewer"
    }
}

val UserTagCustomNavType = object : NavType<UserTag>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: UserTag) {
        bundle.putSerializable(key, Json.encodeToString(value))
    }

    override fun get(bundle: Bundle, key: String): UserTag? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): UserTag {
        return Json.decodeFromString(android.net.Uri.decode(value))
    }

    override fun serializeAsValue(value: UserTag): String {
        return android.net.Uri.encode(Json.encodeToString(value))
    }
}

val MemberListCustomNavType = object : NavType<List<Member>>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: List<Member>) {
        bundle.putSerializable(key, Json.encodeToString(value))
    }

    override fun get(bundle: Bundle, key: String): List<Member>? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): List<Member> {
        return Json.decodeFromString(android.net.Uri.decode(value))
    }

    override fun serializeAsValue(value: List<Member>): String {
        return android.net.Uri.encode(Json.encodeToString(value))
    }
}