package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val email: String?,
    val role : String?,
    val userId : String?
){

    fun getRole() : UserTag {
        return when(role?.lowercase()){
            "owner" -> UserTag.owner
            "member" -> UserTag.member
            "viewer" -> UserTag.viewer
            else -> throw Exception("role not fount.")
        }
    }

}

enum class UserTag {
    owner,
    member,
    viewer
}