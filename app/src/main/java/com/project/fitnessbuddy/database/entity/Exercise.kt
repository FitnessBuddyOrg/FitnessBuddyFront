package com.project.fitnessbuddy.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.fitnessbuddy.screens.common.Language

@Entity(
    tableName = "exercise"
)
data class Exercise(
    val name: String,
    val instructions: String,
    val videoLink: String,
    val category: Category,
    val shareType: ShareType,
    val language: Language,

    @PrimaryKey(autoGenerate = true)
    val memberId: Int? = null
)
