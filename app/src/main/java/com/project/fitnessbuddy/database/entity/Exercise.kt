package com.project.fitnessbuddy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.fitnessbuddy.database.entity.enums.Category
import com.project.fitnessbuddy.database.entity.enums.ShareType
import com.project.fitnessbuddy.database.entity.enums.Language

@Entity(
    tableName = "exercise"
)
data class Exercise(
    val name: String = "",

    val instructions: String = "",

    @ColumnInfo(name = "video_link")
    val videoLink: String = "https://www.youtube.com/watch?v=dGqI0Z5ul4k",

    val category: Category = Category.ABS,

    @ColumnInfo(name = "share_type")
    val shareType: ShareType = ShareType.PRIVATE,

    val language: Language = Language.CUSTOM,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Long? = null
)
