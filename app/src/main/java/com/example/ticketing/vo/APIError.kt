package com.example.ticketing.vo

import com.google.gson.Gson
import kotlinx.serialization.Serializable
import okhttp3.ResponseBody

@Serializable
data class APIErrors(
    val errors : List<APIError>?,
    val error : String?
)

@Serializable
data class APIError(
    val message : String,
    val path : List<String>
)

fun extractError(body : ResponseBody?) : APIErrors {
    if(body != null) {
        val json = body.string()
        val errorObject = Gson().fromJson(
            json,
            APIErrors::class.java
        )
        return errorObject
    }
    return APIErrors(null,  null)
}