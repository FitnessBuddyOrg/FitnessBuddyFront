package com.project.fitnessbuddy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.User

@Dao
interface UserDao {

    @Upsert
    suspend fun upsert(exercise: Exercise): Long

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("SELECT * FROM user WHERE user_id = :userId")
    fun getUser(userId: Long): User
}
