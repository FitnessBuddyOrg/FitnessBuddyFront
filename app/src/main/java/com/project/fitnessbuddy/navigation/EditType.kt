package com.project.fitnessbuddy.navigation

import com.project.fitnessbuddy.screens.common.Functions

enum class EditType {
    ADD,
    EDIT;

    override fun toString(): String {
        return Functions.enumToTitleCase(name)
    }
}
