package com.project.fitnessbuddy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "parameter"
)
data class Parameter(
    @PrimaryKey
    @ColumnInfo(name = "parameter_id")
    val parameterId: String,

    val value: String
)
