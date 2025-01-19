package com.project.fitnessbuddy.screens.exercises

import com.project.fitnessbuddy.database.entity.Category
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.ShareType
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.screens.common.Language

data class ExercisesState(
    val editType: EditType = EditType.ADD,

    val exercises: List<Exercise> = emptyList(),

    val selectedExercise: Exercise = Exercise(
        "",
        "",
        "",
        Category.ABS,
        ShareType.PUBLIC,
        Language.ENGLISH
    ),

    val sortType: SortType = SortType.NAME,
    val searchValue: String = "",
)
