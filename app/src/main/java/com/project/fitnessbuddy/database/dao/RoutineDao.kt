package com.project.fitnessbuddy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.database.entity.Routine
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Upsert
    suspend fun upsert(routine: Routine): Long

    @Update
    suspend fun update(routine: Routine): Int

    @Delete
    suspend fun delete(routine: Routine)

    @Transaction
    @Query("SELECT * FROM routine WHERE user_id = :userId AND name LIKE '%' || :searchValue || '%' AND is_completed = 0")
    fun getTemplateRoutineDTOs(searchValue: String, userId: Long): Flow<List<RoutineDTO>>

    @Transaction
    @Query("SELECT * FROM routine WHERE user_id = :userId AND is_completed = 1")
    fun getCompletedRoutineDTOs(userId: Long): Flow<List<RoutineDTO>>
}
