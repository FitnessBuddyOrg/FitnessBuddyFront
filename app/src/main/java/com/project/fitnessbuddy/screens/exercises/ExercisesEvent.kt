package com.project.fitnessbuddy.screens.exercises

import com.project.fitnessbuddy.database.entity.Category
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.ShareType
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.screens.common.Language

sealed interface ExercisesEvent {
    data object SaveExercise : ExercisesEvent
    data object UpdateExercise : ExercisesEvent

    data class SetName(val name: String) : ExercisesEvent
    data class SetInstructions(val instructions: String) : ExercisesEvent
    data class SetVideoLink(val videoLink: String) : ExercisesEvent
    data class SetCategory(val category: Category) : ExercisesEvent
    data class SetShareType(val shareType: ShareType) : ExercisesEvent
    data class SetLanguage(val language: Language) : ExercisesEvent

    data class SortExercises(val sortType: SortType) : ExercisesEvent

    data class SetEditingExercise(val selectedExercise: Exercise) : ExercisesEvent
    data object ResetEditingExercise : ExercisesEvent

    data class DeleteExercise(val exercise: Exercise) : ExercisesEvent

    data class SetSearchValue(val searchValue: String) : ExercisesEvent

    data class SetEditType(val editType: EditType) : ExercisesEvent
}
