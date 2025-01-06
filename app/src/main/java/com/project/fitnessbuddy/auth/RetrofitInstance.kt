package com.project.fitnessbuddy.auth;


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
private const val BASE_URL = "http://10.0.2.2:8080/"

val authService: AuthService by lazy {
    Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
}
}