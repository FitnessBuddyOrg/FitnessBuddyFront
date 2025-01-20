package com.project.fitnessbuddy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "routine_exercise",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["exercise_id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Routine::class,
            parentColumns = ["routine_id"],
            childColumns = ["routine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["exercise_id"]),
        Index(value = ["routine_id"])
    ]
)
data class RoutineExercise(
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Long,

    @ColumnInfo(name = "routine_id")
    val routineId: Long? = null,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "routine_exercise_id")
    val routineExerciseId: Long? = null
)
