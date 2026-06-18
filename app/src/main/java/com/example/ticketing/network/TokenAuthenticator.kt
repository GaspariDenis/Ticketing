package com.example.ticketing.network

import android.content.SharedPreferences
import com.example.ticketing.repository.AuthRepository
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val apiService: AuthRepository,
    private val sharedPreferences: SharedPreferences
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            val currentAccessToken = sharedPreferences.getString("access_token", null)
            val refreshToken = sharedPreferences.getString("refresh_token", null) ?: return null

            if(currentAccessToken != response.request.header("Authorization")?.removePrefix("Bearer ")){
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            val newTokensResponse = apiService.fetchNewTokens(refreshToken)

            if(newTokensResponse == null) {
                apiService.logoutAccount(refreshToken)
                return null
            }

            sharedPreferences.edit()
                .putString("access_token", newTokensResponse.accessToken)
                .putString("refresh_token", refreshToken)
                .apply()

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokensResponse.accessToken}")
                .build()
        }
    }
}