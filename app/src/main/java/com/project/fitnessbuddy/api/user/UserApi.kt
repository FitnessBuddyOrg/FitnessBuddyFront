package com.project.fitnessbuddy.api.user

import okhttp3.MultipartBody
import retrofit2.http.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class AppOpenDTO (
    val id: Long,
    val userId: Long,
    val openTime: String
){
    fun getParsedOpenTime(): LocalDateTime {
        return LocalDateTime.parse(openTime, DateTimeFormatter.ISO_DATE_TIME)
    }
}

data class UserDTO (
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val provider: String
)

data class UpdateUser (
    val id: Long,
    val name: String,
)

data class ProfilePictureDTO (
    val url: String
)


interface UserApi {

    @POST("user/increment-app-open")
    suspend fun incrementAppOpen()

    @GET("user/app-open-count/{userId}")
    suspend fun getAppOpenCount(@Path("userId") userId: Long): List<AppOpenDTO>

    @GET("user/me")
    suspend fun getMe(): UserDTO

    @PATCH("user/patch")
    suspend fun updateMe(@Body updateUserDTO: UpdateUser): UserDTO

    @GET("user/profile-picture")
    suspend fun getProfilePicture(): ProfilePictureDTO

    @Multipart
    @PATCH("user/profile-picture")
    suspend fun updateProfilePicture(
        @Part file: MultipartBody.Part
    ): ProfilePictureDTO


}
