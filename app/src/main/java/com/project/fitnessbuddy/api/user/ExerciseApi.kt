package com.project.fitnessbuddy.api.user

import com.project.fitnessbuddy.database.entity.enums.Category
import com.project.fitnessbuddy.database.entity.enums.Language
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class ShareExerciseDTO(
    val name: String? = null,
    val instructions: String? = null,
    val videoLink: String? = null,
    val category: Category? = null
)

data class TemplateExerciseDTO(
    val name: String? = null,
    val instructions: String? = null,
    val videoLink: String? = null,
    val category: Category? = null,
    val language: Language? = null,
)

data class TokenResponseDTO(
    val token: String
)

interface ExerciseApi {
    @POST("exercise/share")
    suspend fun shareExercise(@Body shareExerciseDTO: ShareExerciseDTO): TokenResponseDTO

    @GET("exercise/share")
    suspend fun getSharedExercise(@Path("token") token: String): ShareExerciseDTO

    @GET("exercise/templates")
    suspend fun getTemplateExercise(): List<TemplateExerciseDTO>
}
