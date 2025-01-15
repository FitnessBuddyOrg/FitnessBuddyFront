package com.project.fitnessbuddy.screens.common

import com.project.fitnessbuddy.R

enum class Language(val resourceId: Int, val localeString: String) {
    ENGLISH(R.string.english, "gb"),
    FRENCH(R.string.french, "fr"),
    RUSSIAN(R.string.russian, "ru");

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

        fun getLanguage(languageString: String): Language {
            Language.entries.filter { it.name == languageString }.forEach {
                return it
            }
            return ENGLISH
        }
    }
}
