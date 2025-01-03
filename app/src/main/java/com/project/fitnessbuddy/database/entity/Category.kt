package com.project.fitnessbuddy.database.entity

import com.project.fitnessbuddy.screens.common.Functions

enum class Category {
    ARMS,
    BACK,
    CHEST,
    LEGS,
    SHOULDERS,
    ABS;

    override fun toString(): String {
        return Functions.enumToTitleCase(name)
    }
}


