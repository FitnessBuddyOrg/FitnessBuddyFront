package com.project.fitnessbuddy.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "parameter"
)
data class Parameter(
    @PrimaryKey
    val parameterId: String,

    val value: String
)
