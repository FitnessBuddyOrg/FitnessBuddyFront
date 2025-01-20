package com.project.fitnessbuddy.screens.routines

import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.database.dto.RoutineExerciseDTO
import com.project.fitnessbuddy.database.dto.RoutineExerciseSetDTO
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.Routine
import com.project.fitnessbuddy.database.entity.RoutineExercise
import com.project.fitnessbuddy.database.entity.enums.Frequency
import com.project.fitnessbuddy.database.entity.enums.Language
import com.project.fitnessbuddy.database.entity.enums.ShareType
import com.project.fitnessbuddy.navigation.EditType
import java.util.Date

sealed interface RoutinesEvent {
    data object UpsertSelectedRoutineDTO : RoutinesEvent
    data class UpsertRoutineDTO(val routineDTO: RoutineDTO) : RoutinesEvent

    data class SetName(val name: String) : RoutinesEvent
    data class SetFrequency(val frequency: Frequency) : RoutinesEvent
    data class SetShareType(val shareType: ShareType) : RoutinesEvent
    data class SetLanguage(val language: Language) : RoutinesEvent
    data class SetStartDate(val startDate: Date) : RoutinesEvent

    data class SetSelectedRoutineDTO(val routineDTO: RoutineDTO) : RoutinesEvent
    data object ResetSelectedRoutineDTO : RoutinesEvent

    data class HandleExercise(val exercise: Exercise, val selected: Boolean) : RoutinesEvent
    data object ClearExercisesLists : RoutinesEvent

    data class RemoveRoutineExerciseDTO(val routineExerciseDTO: RoutineExerciseDTO) : RoutinesEvent
    data object ApplyExercises : RoutinesEvent

    data class AddRoutineExerciseSet(val routineExercise: RoutineExercise) : RoutinesEvent

    data class RemoveRoutineExerciseSet(
        val routineExercise: RoutineExercise,
        val routineExerciseSetDTO: RoutineExerciseSetDTO
    ) : RoutinesEvent

    data class UpdateRoutineExerciseSet(
        val routineExerciseSetDTO: RoutineExerciseSetDTO,
        val routineExercise: RoutineExercise
    ) : RoutinesEvent

    data class DeleteRoutine(val routine: Routine) : RoutinesEvent
    data object CompleteRoutine : RoutinesEvent

    data class SetSearchValue(val searchValue: String) : RoutinesEvent
    data class SetEditType(val editType: EditType) : RoutinesEvent
}
