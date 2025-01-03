package com.project.fitnessbuddy.screens.common

enum class Language {
    ENGLISH,
    FRENCH,
    RUSSIAN;

    override fun toString(): String {
        return Functions.enumToTitleCase(name)
    }
}
