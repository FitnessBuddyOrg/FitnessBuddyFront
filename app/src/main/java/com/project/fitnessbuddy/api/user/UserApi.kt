package com.project.fitnessbuddy.api.user

import retrofit2.http.*

interface UserApi {

    @POST("user/increment-app-open")
    suspend fun incrementAppOpen()

    @GET("user//app-open-count")
    suspend fun getAppOpenCount(@Path("userId") userId: Long): Int
}
