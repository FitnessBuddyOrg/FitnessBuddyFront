package com.project.fitnessbuddy.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.project.fitnessbuddy.model.Exercise

@Dao
interface ExerciseDao {

    @Upsert
    suspend fun upsert(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)
}
