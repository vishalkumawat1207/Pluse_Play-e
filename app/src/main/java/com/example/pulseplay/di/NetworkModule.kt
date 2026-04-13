package com.example.pulseplay.di

import com.example.pulseplay.data.apiService.ApiService
import com.example.pulseplay.data.apiService.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("auth") retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)


    @Provides
    @Singleton
    @Named("api")
    fun provideApiRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(@Named("api") retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}

