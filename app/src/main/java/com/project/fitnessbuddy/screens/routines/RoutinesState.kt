package com.project.fitnessbuddy.screens.routines

import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.navigation.EditType

data class RoutinesState(
    val editType: EditType = EditType.ADD,

    val routineDTOs: List<RoutineDTO> = emptyList(),

    val selectedRoutineDTO: RoutineDTO = RoutineDTO(),

    val searchValue: String = "",
)


