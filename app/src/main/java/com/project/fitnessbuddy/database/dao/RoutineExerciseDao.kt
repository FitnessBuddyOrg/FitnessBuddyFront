package com.project.fitnessbuddy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.project.fitnessbuddy.database.entity.RoutineExercise

@Dao
interface RoutineExerciseDao {
    @Upsert
    suspend fun upsert(routineExercise: RoutineExercise): Long

    @Delete
    suspend fun delete(routineExercise: RoutineExercise)
}
