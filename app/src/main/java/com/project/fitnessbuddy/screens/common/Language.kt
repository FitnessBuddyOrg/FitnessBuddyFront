package com.project.fitnessbuddy.screens.common

enum class Language(val localeString: String) {
    ENGLISH("en"),
    FRENCH("fr"),
    RUSSIAN("ru");

    override fun toString(): String {
        return Functions.enumToTitleCase(name)
    }

    companion object {
        fun getLocaleString(languageString: String): String {
            Language.entries.filter { it.name == languageString }.forEach {
                return it.localeString
            }
            return ENGLISH.localeString
        }
    }
}
