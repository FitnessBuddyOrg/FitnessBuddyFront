package com.project.fitnessbuddy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val description: String,
    val image: Int,
    val video: Int,
    val equipment: String,
    val type: String,
    val difficulty: String,
    val muscleGroup: String
)
