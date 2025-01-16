package com.project.fitnessbuddy.database.entity.enums

import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.screens.common.Functions

enum class Category(val resourceId: Int) {
    ARMS(R.string.arms),
    BACK(R.string.back),
    CHEST(R.string.chest),
    LEGS(R.string.legs),
    SHOULDERS(R.string.shoulders),
    ABS(R.string.abs),;

    override fun toString(): String {
        return Functions.enumToTitleCase(name)
    }
}


