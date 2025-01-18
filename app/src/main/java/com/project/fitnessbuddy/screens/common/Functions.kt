package com.project.fitnessbuddy.screens.common

class Functions {
    companion object {
        fun enumToTitleCase(name: String): String {
            return name.lowercase().replaceFirstChar { it.uppercase() }
        }

        fun generateRandomLong(): Long {
            return (0..Long.MAX_VALUE).random()
        }
    }
}
