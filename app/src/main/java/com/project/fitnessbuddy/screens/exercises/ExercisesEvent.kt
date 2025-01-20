package com.project.fitnessbuddy.screens.exercises

import com.project.fitnessbuddy.database.entity.enums.Category
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.enums.ShareType
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.database.entity.enums.Language

sealed interface ExercisesEvent {
    data object UpsertExercise : ExercisesEvent

    data class SetName(val name: String) : ExercisesEvent
    data class SetInstructions(val instructions: String) : ExercisesEvent
    data class SetVideoLink(val videoLink: String) : ExercisesEvent
    data class SetCategory(val category: Category) : ExercisesEvent
    data class SetShareType(val shareType: ShareType) : ExercisesEvent
    data class SetLanguage(val language: Language) : ExercisesEvent

    data class SortExercises(val sortType: SortType) : ExercisesEvent

    data class SetSelectedExercise(val selectedExercise: Exercise) : ExercisesEvent

    data class DeleteExercise(val exercise: Exercise) : ExercisesEvent

    data class SetSearchValue(val searchValue: String) : ExercisesEvent

    data class SetEditType(val editType: EditType) : ExercisesEvent

    data object ShareSelectedExercise : ExercisesEvent
    data object ClearSharedExerciseToken: ExercisesEvent

    data class SetFetchedExerciseToken(val fetchedExerciseToken: String) : ExercisesEvent
    data object FetchExerciseByToken: ExercisesEvent
    data object ResetFetching: ExercisesEvent
}
