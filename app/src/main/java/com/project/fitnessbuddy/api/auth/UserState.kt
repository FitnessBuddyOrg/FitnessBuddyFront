package com.project.fitnessbuddy.api.auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf

data class UserState(
    val accessToken: String? = null,
    val isLoggedIn: Boolean = false,
    val email: String? = null,
    val name: String? = null,
    val id: Long? = null

)

val LocalUserState = staticCompositionLocalOf { mutableStateOf(UserState()) }
