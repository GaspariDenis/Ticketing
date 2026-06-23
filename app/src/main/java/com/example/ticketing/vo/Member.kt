package com.example.ticketing.vo

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val email: String? = null,
    val role : String? = null,
    val userId : String? = null,
    val user : User? = null
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

fun stringFromTag(tag: UserTag) : String{
    return when(tag){
        UserTag.owner -> "owner"
        UserTag.member -> "member"
        UserTag.viewer -> "viewer"
    }
}