package com.project.fitnessbuddy.api.auth

import com.project.fitnessbuddy.database.entity.User

sealed interface AuthEvent {
    data object UpsertUser : AuthEvent
}
