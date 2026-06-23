package com.example.ticketing.network

import android.content.SharedPreferences
import android.util.Log
import com.example.ticketing.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokens: AuthRepository
) : Interceptor {
    private val tag = "AuthInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        var accessToken : String
        runBlocking {
            withContext(Dispatchers.IO){
                try{
                    accessToken = tokens.dao.getLocalToken().accessToken
                }catch (e : Exception) {
                    Log.e( tag, e.message ?: "Unexpected error.")
                    accessToken = ""
                }
            }
        }
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }
}