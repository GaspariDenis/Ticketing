package com.example.ticketing.network

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokens: SharedPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = tokens.getString("access_token", "") ?: ""
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }
}