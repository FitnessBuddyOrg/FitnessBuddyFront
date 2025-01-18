package com.project.fitnessbuddy.database.dto

import androidx.room.Embedded
import androidx.room.Ignore
import com.project.fitnessbuddy.database.entity.RoutineExerciseSet
import com.project.fitnessbuddy.screens.common.Functions.Companion.generateRandomLong

data class RoutineExerciseSetDTO @JvmOverloads constructor(
    @Embedded
    val routineExerciseSet: RoutineExerciseSet,

    @Ignore
    var tempId: Long = generateRandomLong()
) {
    val weight get(): Int = routineExerciseSet.weight
    val reps get(): Int = routineExerciseSet.reps
}
