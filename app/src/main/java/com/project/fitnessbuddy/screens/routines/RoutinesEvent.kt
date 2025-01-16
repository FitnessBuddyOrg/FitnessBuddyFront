package com.project.fitnessbuddy.screens.routines

import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.database.entity.Routine
import com.project.fitnessbuddy.database.entity.enums.Frequency
import com.project.fitnessbuddy.database.entity.enums.Language
import com.project.fitnessbuddy.database.entity.enums.ShareType
import com.project.fitnessbuddy.navigation.EditType

sealed interface RoutinesEvent {
    data object SaveRoutine : RoutinesEvent
    data object UpdateRoutine : RoutinesEvent

    data class SetName(val name: String) : RoutinesEvent
    data class SetFrequency(val frequency: Frequency) : RoutinesEvent
    data class SetShareType(val shareType: ShareType) : RoutinesEvent
    data class SetLanguage(val language: Language) : RoutinesEvent

    data class SetSelectedRoutineDTO(val selectedRoutineDTO: RoutineDTO) : RoutinesEvent
    data object ResetSelectedRoutineDTO : RoutinesEvent

    data class DeleteRoutineDTO(val routineDTO: Routine) : RoutinesEvent

    data class SetSearchValue(val searchValue: String) : RoutinesEvent

    data class SetEditType(val editType: EditType) : RoutinesEvent
}
