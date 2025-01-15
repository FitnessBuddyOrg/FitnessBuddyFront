package com.project.fitnessbuddy.navigation

import androidx.compose.runtime.Composable

class AppRoute(
    val routeName: String,
    val name: String,
    val subRoutes: List<AppRoute> = listOf(),
    val startDestination: String = routeName,
    val screen: @Composable (() -> Unit)? = {},
    val icon: @Composable (() -> Unit)? = {},
)
