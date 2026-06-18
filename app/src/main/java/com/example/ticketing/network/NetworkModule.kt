package com.example.ticketing.network

import android.content.SharedPreferences
import com.example.ticketing.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "localhost:3000"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(apiService: AuthRepository, sharedPreferences: SharedPreferences) : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sharedPreferences))
            .authenticator(TokenAuthenticator(apiService, sharedPreferences))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) : APIService {
        return retrofit.create(APIService::class.java)
    }
}