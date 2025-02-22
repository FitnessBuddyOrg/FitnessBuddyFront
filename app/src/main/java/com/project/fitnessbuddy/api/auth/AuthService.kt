package com.project.fitnessbuddy.api.auth

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(val email: String, val password: String, val confirmPassword: String)

data class GoogleLoginRequest(
    val idToken: String,
    val profilePictureUrl: String,
)

data class UserResponse(
    @SerializedName("accessToken") val accessToken: String,
    val email: String,
    val id : Long
)
data class GitHubTokenRequestDTO(val code: String)



interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): UserResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    @POST("auth/login/oauth2/code/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): UserResponse

    @POST("auth/login/oauth2/code/github")
    suspend fun githubLogin(@Body request: GitHubTokenRequestDTO): UserResponse

}

