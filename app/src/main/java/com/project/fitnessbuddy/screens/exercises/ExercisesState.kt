package com.project.fitnessbuddy.screens.exercises

import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.navigation.EditType

data class ExercisesState(
    val editType: EditType = EditType.ADD,

    val exercises: List<Exercise> = emptyList(),

    val selectedExercise: Exercise = Exercise(),

    val sortType: SortType = SortType.NAME,
    val searchValue: String = "",
)
