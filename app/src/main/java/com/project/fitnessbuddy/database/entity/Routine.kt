package com.project.fitnessbuddy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.project.fitnessbuddy.database.entity.enums.Frequency
import com.project.fitnessbuddy.database.entity.enums.Language
import com.project.fitnessbuddy.database.entity.enums.ShareType
import java.util.Date

@Entity(
    tableName = "routine",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Routine(
    val name: String = "",

    val frequency: Frequency = Frequency.WEEKLY,

    @ColumnInfo(name = "share_type")
    val shareType: ShareType = ShareType.PRIVATE,

    val language: Language = Language.CUSTOM,

    @ColumnInfo(name = "last_performed")
    val lastPerformed: Date? = null,

    @ColumnInfo(name = "is_completed", defaultValue = "0")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "start_date")
    val startDate: Date? = null,

    @ColumnInfo(name = "end_date")
    val endDate: Date? = null,

    @ColumnInfo(name = "user_id")
    val userId: Long? = null,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "routine_id")
    val routineId: Long? = null
)
