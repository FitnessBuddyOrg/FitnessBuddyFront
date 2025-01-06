package com.project.fitnessbuddy.auth

import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String, val confirmPassword: String)
data class UserResponse(val accessToken: String)

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): UserResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse
}

