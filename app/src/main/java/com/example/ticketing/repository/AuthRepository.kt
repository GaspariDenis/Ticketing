package com.example.ticketing.repository

import android.util.Log
import androidx.room.Room
import com.example.ticketing.App
import com.example.ticketing.data.Database
import com.example.ticketing.network.APIService
import com.example.ticketing.network.APIStatus
import com.example.ticketing.network.RefreshTokenRequest
import com.example.ticketing.vo.DbToken
import com.example.ticketing.vo.RegisterUser
import com.example.ticketing.vo.User
import com.example.ticketing.vo.UserToken
import com.example.ticketing.vo.extractError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(val api : APIService) {

    private val tag = "AuthRepo"

    private val db = Room.databaseBuilder(
        App.appContext,
        Database::class.java, "database"
    ).build()

    val dao = db.appDao()


    suspend fun getUserDb()  : UserToken {
        val dbValue : DbToken
        withContext(Dispatchers.IO){
            dbValue = dao.getLocalToken()
        }

        return UserToken(
            id = dbValue.userId,
            refreshToken = dbValue.refreshToken,
            accessToken = dbValue.accessToken
        )
    }

    suspend fun isLogged() : Boolean {
        try {
            val tmp : DbToken
            withContext(Dispatchers.IO){
                tmp = dao.getLocalToken()
            }
            Log.d(tag, "Return ${tmp.userId != ""}.")
            return tmp.userId != ""
        }catch (e : Exception){
            Log.w(tag, "${e.message ?: "Unexpected Error"}, return false" )
            return false
        }
    }

    suspend fun fetchNewTokens(refreshToken: String) : APIStatus<UserToken?> {
        return try {
            val dbToken : DbToken
            withContext(Dispatchers.IO){
                dbToken = dao.getLocalToken()
            }

            val response = api.getNewTokens(RefreshTokenRequest(refreshToken))

            return when(response.code()) {
                200 -> {
                    if(response.body() != null) {
                        withContext(Dispatchers.IO){
                            dao.updateAccessToken(
                                DbToken(
                                    dbToken.userId,
                                    refreshToken,
                                    response.body()?.accessToken ?: throw Exception("The access Token was null inside the response body.")
                                )
                            )
                        }

                        APIStatus.Success(UserToken(
                            refreshToken = refreshToken,
                            accessToken =response.body()?.accessToken,
                            id = response.body()?.id
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

    suspend fun logoutAccount() : APIStatus<Unit> {
        return try {
            val dbToken = dao.getLocalToken()
            val response = api.logoutAccount(UserToken(
                accessToken = dbToken.accessToken,
                refreshToken = dbToken.refreshToken,
                id = dbToken.userId
            ))

            when(response.code()) {
                204 -> {
                    Log.d(tag, "Logout successfully.")
                    dao.removeLocalToken(dbToken.userId)
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

            try {
                val local = dao.getLocalToken()
                dao.removeLocalToken(local.userId)
            }catch (e : Exception) {
                Log.e(tag, e.message ?: "Unknown error.")
            }

            return when(response.code()) {
                200 -> {
                    val body = response.body() ?: throw Exception("the body was empty.")

                    Log.d(tag, body.toString())

                    try{
                        dao.insertLocalToken(DbToken(
                            body.id ?: "",
                            body.refreshToken ?: "",
                            body.accessToken ?: ""))
                    }catch (e : Exception) {
                        Log.e(tag, e.message ?: "Unexpected error while saving UserToken.")
                    }

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
                201 -> {
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