package com.project.fitnessbuddy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.project.fitnessbuddy.database.entity.abstracts.ListedEntity
import com.project.fitnessbuddy.database.entity.enums.Category
import com.project.fitnessbuddy.database.entity.enums.ShareType
import com.project.fitnessbuddy.database.entity.enums.Language

@Entity(
    tableName = "exercise",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"])
    ]
)
data class Exercise(
    override val name: String = "",

    val instructions: String = "",

    @ColumnInfo(name = "video_link")
    val videoLink: String = "https://www.youtube.com/watch?v=dGqI0Z5ul4k",

    val category: Category = Category.ABS,

    @ColumnInfo(name = "share_type")
    override val shareType: ShareType = ShareType.PRIVATE,

    override val language: Language = Language.CUSTOM,

    @ColumnInfo(name = "user_id")
    val userId: Long? = null,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Long? = null
): ListedEntity
