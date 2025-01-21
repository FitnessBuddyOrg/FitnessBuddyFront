package com.project.fitnessbuddy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.project.fitnessbuddy.database.entity.Parameter

@Dao
interface ParameterDao {

    @Upsert
    suspend fun upsert(parameter: Parameter)

    @Delete
    suspend fun delete(parameter: Parameter)

    @Transaction
    @Query("SELECT * FROM parameter WHERE parameter_id = :parameterId")
    suspend fun getParameterById(parameterId: String): Parameter?
}
