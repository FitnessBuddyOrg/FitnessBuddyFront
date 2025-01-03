package com.project.fitnessbuddy.screens.common

class Functions {
    companion object {
        fun enumToTitleCase(name: String): String {
            return name.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}
