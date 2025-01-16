package com.project.fitnessbuddy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.fitnessbuddy.database.entity.enums.Frequency
import com.project.fitnessbuddy.database.entity.enums.Language
import com.project.fitnessbuddy.database.entity.enums.ShareType
import java.util.Date

@Entity(
    tableName = "routine"
)
data class Routine(
    val name: String = "",

    val frequency: Frequency = Frequency.WEEKLY,

    @ColumnInfo(name = "share_type")
    val shareType: ShareType = ShareType.PRIVATE,

    val language: Language = Language.CUSTOM,

    @ColumnInfo(name = "last_performed")
    val lastPerformed: Date? = null,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "routine_id")
    val routineId: Long? = null
)
