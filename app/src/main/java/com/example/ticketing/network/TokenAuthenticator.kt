package com.example.ticketing.network

import android.content.SharedPreferences
import android.util.Log
import com.example.ticketing.vo.UserToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

private const val tag = "TokenAuthenticator"

class TokenAuthenticator(
    private val apiService: APIService,
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

            val newTokensResponse = runBlocking {
                withContext(Dispatchers.IO){
                    apiService.getNewTokens(refreshToken)
                }
            }

            val token = try{ when(newTokensResponse.code()){
                200 -> newTokensResponse.body() ?: UserToken(refreshToken, null)
                400, 401 -> {
                    runBlocking {
                        withContext(Dispatchers.IO){
                            apiService.logoutAccount(refreshToken)
                        }
                    }
                    return null
                }
                else -> {
                    UserToken(refreshToken, null)
                }
            }}catch (e : Exception){
                Log.e(tag, e.message ?: "Unexpected error.")
                UserToken(refreshToken, null)
            }

            sharedPreferences.edit()
                .putString("access_token", token.accessToken)
                .putString("refresh_token", token.refreshToken)
                .apply()

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${token.accessToken}")
                .build()
        }
    }
}