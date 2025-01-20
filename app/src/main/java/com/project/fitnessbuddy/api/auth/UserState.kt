package com.project.fitnessbuddy.api.auth

import com.project.fitnessbuddy.database.entity.User

data class UserState(
    val user: User = User(),
//    val accessToken: String? = null,
//    val email: String? = null,
//    val name: String? = null,
//    val userId: Long? = null,

    val profilePictureUrl: String? = null,
    val isLoggedIn: Boolean = false
)
