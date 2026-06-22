package com.example.ticketing.network

import android.content.SharedPreferences
import com.example.ticketing.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokens: AuthRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken : String
        runBlocking {
            withContext(Dispatchers.IO){
                accessToken = tokens.dao.getLocalToken().accessToken
            }
        }
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }
}