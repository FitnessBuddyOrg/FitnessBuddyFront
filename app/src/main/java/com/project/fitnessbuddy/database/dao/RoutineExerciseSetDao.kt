package com.project.fitnessbuddy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.project.fitnessbuddy.database.entity.RoutineExerciseSet

@Dao
interface RoutineExerciseSetDao {

    @Upsert
    suspend fun upsert(routineExerciseSet: RoutineExerciseSet): Long

    @Delete
    suspend fun delete(routineExerciseSet: RoutineExerciseSet)
}
