package com.example.ticketing.repository

import android.util.Log
import com.example.ticketing.network.APIService
import com.example.ticketing.network.UserToken
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthRepository(val api : APIService) {

    private val tag = "AuthRepo"

    @Singleton
    fun fetchNewTokens(refreshToken: String) : UserToken? {
        return runBlocking {
            try {
                UserToken(
                    refreshToken = refreshToken,
                    accessToken = api.getNewTokens(refreshToken)
                )
            }catch (e:  Exception){
                Log.e(tag, e.message ?: "Unexpected error.")
                null
            }
        }
    }

    @Singleton
    fun logoutAccount(refreshToken: String) {
        runBlocking {
            try {
                api.logoutAccount(refreshToken)
            }catch (e : Exception) {
                Log.e(tag, e.message ?: "Unexpected error.")
            }
        }
    }
}