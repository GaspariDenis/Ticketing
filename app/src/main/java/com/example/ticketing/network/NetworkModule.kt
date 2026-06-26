package com.example.ticketing.network

import android.content.Context
import android.content.SharedPreferences
import com.example.ticketing.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

private const val BASE_URL = "http://172.20.10.2:3000/"
private const val REFRESH = "refresh"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context) : SharedPreferences =
        context.getSharedPreferences("ticketing_prefs", Context.MODE_PRIVATE)

    //client for refreshing token

    @Provides
    @Singleton
    @Named(REFRESH)
    fun provideRefreshOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().build()

    @Provides
    @Singleton
    @Named(REFRESH)
    fun provideRefreshRetrofit(@Named(REFRESH) okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named(REFRESH)
    fun provideRefreshApiService(@Named(REFRESH) retrofit: Retrofit) : APIService =
        retrofit.create(APIService::class.java)

    @Provides
    @Singleton
    @Named(REFRESH)
    fun provideRefreshBackend(@Named(REFRESH) apiService: APIService) : AuthRepository =
        AuthRepository(apiService)

    //Authenticator
    @Provides
    @Singleton
    fun provideTokenAuthenticator(
    @Named(REFRESH) apiService: AuthRepository
    ) : TokenAuthenticator =
        TokenAuthenticator(apiService)

    //Authenticated client

    @Provides
    @Singleton
    fun provideOkHttpClient(authenticator: TokenAuthenticator, @Named(REFRESH)repo : AuthRepository) : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(repo))
            .authenticator(authenticator)
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