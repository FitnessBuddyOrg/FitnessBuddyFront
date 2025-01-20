package com.project.fitnessbuddy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.project.fitnessbuddy.database.entity.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Upsert
    suspend fun upsert(exercise: Exercise): Long

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("SELECT * FROM exercise WHERE name LIKE '%' || :searchValue || '%' ORDER BY name ASC")
    fun getExercisesOrderedByName(searchValue: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercise WHERE name LIKE '%' || :searchValue || '%' ORDER BY category ASC")
    fun getExercisesOrderedByCategory(searchValue: String): Flow<List<Exercise>>
}
