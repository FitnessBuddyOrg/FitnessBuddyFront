package com.project.fitnessbuddy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.database.entity.Routine
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Upsert
    suspend fun upsert(routine: Routine): Long

    @Delete
    suspend fun delete(routine: Routine)

    @Transaction
    @Query("SELECT * FROM routine WHERE name LIKE '%' || :searchValue || '%'")
    fun getRoutineDTOs(searchValue: String): Flow<List<RoutineDTO>>
}
