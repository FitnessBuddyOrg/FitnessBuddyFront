package com.project.fitnessbuddy.screens.exercises

import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.screens.common.SortingState

data class ExercisesState(
    val editType: EditType = EditType.ADD,

    val exercises: List<Exercise> = emptyList(),

    val selectedExercise: Exercise = Exercise(),

    override val sortType: SortType = SortType.NAME,
    val searchValue: String = "",

    val sharedExerciseToken: String = "",
): SortingState
