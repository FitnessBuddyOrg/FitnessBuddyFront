package com.project.fitnessbuddy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "routine_exercise_set",
    foreignKeys = [ForeignKey(
        entity = RoutineExercise::class,
        parentColumns = ["routine_exercise_id"],
        childColumns = ["routine_exercise_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["routine_exercise_id"])
    ]
)
data class RoutineExerciseSet(
    val weight: Int,
    val reps: Int,

    @ColumnInfo(name = "routine_exercise_id")
    val routineExerciseId: Long? = null,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "routine_exercise_set_id")
    val routineExerciseSetId: Long? = null
)
