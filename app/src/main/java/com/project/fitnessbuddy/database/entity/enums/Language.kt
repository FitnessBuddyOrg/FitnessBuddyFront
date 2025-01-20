package com.project.fitnessbuddy.database.entity.enums

import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.screens.common.Functions

enum class Language(val resourceId: Int, val localeString: String, val isCustom: Boolean = false) {
    ENGLISH(R.string.english, "gb"),
    FRENCH(R.string.french, "fr"),
    RUSSIAN(R.string.russian, "ru"),

    CUSTOM(R.string.custom, "custom", true);

    override fun toString(): String {
        return Functions.enumToTitleCase(name)
    }

    companion object {
        fun getLanguage(languageString: String): Language {
            Language.entries.filter { it.name == languageString }.forEach {
                return it
            }
            return ENGLISH
        }
    }
}
