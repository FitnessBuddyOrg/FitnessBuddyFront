package com.project.fitnessbuddy.navigation

import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.screens.common.Functions

enum class EditType(val resourceId: Int) {
    ADD(R.string.add),
    EDIT(R.string.edit);

    override fun toString(): String {
        return Functions.enumToTitleCase(name)
    }
}
