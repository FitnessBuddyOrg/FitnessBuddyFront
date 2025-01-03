package com.project.fitnessbuddy.database.entity

import com.project.fitnessbuddy.screens.common.Functions

enum class ShareType {
    PUBLIC,
    PRIVATE,
    SHARED;

    override fun toString(): String {
        return Functions.enumToTitleCase(name)
    }
}


