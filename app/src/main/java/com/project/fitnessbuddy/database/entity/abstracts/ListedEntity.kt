package com.project.fitnessbuddy.database.entity.abstracts

import com.project.fitnessbuddy.database.entity.enums.Language
import com.project.fitnessbuddy.database.entity.enums.ShareType

interface ListedEntity {
    val name: String
    val language: Language
    val shareType: ShareType
}
