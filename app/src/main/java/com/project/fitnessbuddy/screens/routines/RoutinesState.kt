package com.project.fitnessbuddy.screens.routines

import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.navigation.EditType

data class RoutinesState(
    val editType: EditType = EditType.ADD,

    val templateRoutineDTOs: List<RoutineDTO> = emptyList(),
    val completedRoutineDTOs: List<RoutineDTO> = emptyList(),

    val potentialExercisesToAdd: MutableList<Exercise> = mutableListOf(),
    val existingExercisesToRemove: MutableList<Exercise> = mutableListOf(),

    val selectedRoutineDTO: RoutineDTO = RoutineDTO(),

    val searchValue: String = "",
)


