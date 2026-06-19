package com.example.ticketing.repository

import android.util.Log
import com.example.ticketing.network.APIService
import com.example.ticketing.network.APIStatus
import com.example.ticketing.network.RegisterUser
import com.example.ticketing.network.User
import com.example.ticketing.network.UserToken
import com.example.ticketing.network.extractError
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthRepository @Inject constructor(val api : APIService) {

    private val tag = "AuthRepo"

    @Singleton
    suspend fun fetchNewTokens(refreshToken: String) : APIStatus<UserToken?> {
        return try {
            val response = api.getNewTokens(refreshToken)

            return when(response.code()) {
                200 -> {
                    if(response.body() != null) {
                        APIStatus.Success(UserToken(
                            refreshToken = refreshToken,
                            accessToken =response.body()?.accessToken
                        ))
                    }

                    APIStatus.Loading
                }
                401 -> {
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError(response.errorBody())
                    )
                }
                else ->{
                    throw Exception("Error with code ${response.code()}, it's not handle.")
                }
            }
        }catch (e:  Exception){
            Log.e(tag, e.message ?: "Unexpected error.")
            APIStatus.Error(e)
        }
    }

    @Singleton
    suspend fun logoutAccount(refreshToken: String) {
        try {
            val response = api.logoutAccount(refreshToken)

            when(response.code()) {
                204 -> return //successful logout
                401 -> throw Exception(extractError(response.errorBody()).error ?: "The format error for the given code was different from the documentation")
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception) {
            Log.e(tag, e.message ?: "Unexpected error.")
        }
    }

    @Singleton
    suspend fun loginAccount(user : User) : APIStatus<UserToken> {
        return try {
            val response = api.loginAccount(user)

            return when(response.code()) {
                200 -> {
                    val body = response.body() ?: throw Exception("the body was empty.")
                    APIStatus.Success(body)
                }

                400, 401 -> {
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError(response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error.")
            APIStatus.Error(e)
        }
    }

    @Singleton
    suspend fun registerAccount(account : RegisterUser) : APIStatus<User> {
        return try {
            val response = api.registerAccount(account)

            return when(response.code()) {
                200 -> {
                    val body = response.body() ?: throw Exception("the body was empty.")
                    APIStatus.Success(body)
                }

                400, 422 -> {
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError(response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error.")
            APIStatus.Error(e)
        }
    }
}