package com.project.fitnessbuddy.screens.common

import com.project.fitnessbuddy.database.entity.Parameter

data class ParametersState(
    val languageParameter: Parameter = Parameter(LANGUAGE_ID, Language.ENGLISH.name)
)


const val LANGUAGE_ID = "language"

