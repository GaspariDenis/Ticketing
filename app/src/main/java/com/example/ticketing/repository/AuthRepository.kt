package com.example.ticketing.repository

import android.util.Log
import com.example.ticketing.network.APIService
import com.example.ticketing.network.APIStatus
import com.example.ticketing.vo.RegisterUser
import com.example.ticketing.vo.User
import com.example.ticketing.vo.UserToken
import com.example.ticketing.vo.extractError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(val api : APIService) {

    private val tag = "AuthRepo"

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

    suspend fun logoutAccount(refreshToken: String) : APIStatus<Unit> {
        return try {
            val response = api.logoutAccount(refreshToken)

            when(response.code()) {
                204 -> {
                    Log.d(tag, "Logout successfully.")
                    APIStatus.Success(Unit)
                }
                401 -> APIStatus.ErrorAPI(
                    code = response.code(),
                    error = extractError(response.errorBody())
                )
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception) {
            Log.e(tag, e.message ?: "Unexpected error.")
            APIStatus.Error(e)
        }
    }

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