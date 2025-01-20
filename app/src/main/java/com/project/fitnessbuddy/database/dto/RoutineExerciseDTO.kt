package com.project.fitnessbuddy.database.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.RoutineExercise
import com.project.fitnessbuddy.database.entity.RoutineExerciseSet

data class RoutineExerciseDTO(
    @Embedded
    val routineExercise: RoutineExercise,

    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "exercise_id"
    )
    val exercise: Exercise = Exercise(),

    @Relation(
        parentColumn = "routine_exercise_id",
        entityColumn = "routine_exercise_id",
        entity = RoutineExerciseSet::class
    )
    val routineExerciseSetDTOs: List<RoutineExerciseSetDTO> = emptyList()
)
