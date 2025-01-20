package com.project.fitnessbuddy.database.entity.enums

import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.screens.common.Functions

enum class ShareType(val resourceId: Int) {
    PUBLIC(R.string.public_res),
    PRIVATE(R.string.private_res),
    SHARED(R.string.shared_res);

    override fun toString(): String {
        return Functions.enumToTitleCase(name)
    }
}


