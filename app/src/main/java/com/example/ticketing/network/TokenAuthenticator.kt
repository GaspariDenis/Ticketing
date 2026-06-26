package com.example.ticketing.network

import android.util.Log
import com.example.ticketing.repository.AuthRepository
import com.example.ticketing.vo.DbToken
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
    private val apiService: AuthRepository
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            Log.d(tag, "called token authenticator.")

            var currentAccessToken : String
            var refreshToken : String

            runBlocking {
                withContext(Dispatchers.IO){
                    try {
                        val tmp = apiService.dao.getLocalToken()
                        currentAccessToken = tmp.accessToken
                        refreshToken = tmp.refreshToken
                    }catch (e : Exception) {
                        Log.e(tag, e.message ?: "Unexpected error.")
                        currentAccessToken = ""
                        refreshToken = ""
                    }
                }
            }

            Log.d(tag, "current access token -> $currentAccessToken")
            Log.d(tag, response.request.header("Authorization")?.removePrefix("Bearer ") ?: "")

            if(currentAccessToken != response.request.header("Authorization")?.removePrefix("Bearer ")){
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            //UserToken
            val status : APIStatus<UserToken?>
            runBlocking {
                withContext(Dispatchers.IO){
                    status = apiService.fetchNewTokens(refreshToken)
                }
            }

            val token = when(status){
                is APIStatus.Success -> {
                    status.data ?: throw Exception("the body was empty.")
                }
                is APIStatus.ErrorAPI -> {throw Exception(status.errorMessage())}
                is APIStatus.Error -> {throw Exception(status.e.message ?: "Unknown message.")}
                else -> UserToken("", "" , "")
            }

            Log.d(tag, token.toString())

            runBlocking {
                withContext(Dispatchers.IO){
                    apiService.dao.updateAccessToken(DbToken(
                        userId = token.id ?: "",
                        accessToken = token.accessToken ?: "",
                        refreshToken = token.refreshToken ?: ""
                    ))
                }
            }

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${token.accessToken}")
                .build()
        }
    }
}