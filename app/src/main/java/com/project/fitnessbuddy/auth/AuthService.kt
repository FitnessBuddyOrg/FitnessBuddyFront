package com.project.fitnessbuddy.auth

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String, val confirmPassword: String)
data class GoogleLoginRequest(val idToken: String)
data class UserResponse(
    @SerializedName("accessToken") val accessToken: String,
    val email: String
)

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): UserResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    @POST("auth/login/oauth2/code/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): UserResponse
}

