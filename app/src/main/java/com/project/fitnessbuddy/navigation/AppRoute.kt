package com.project.fitnessbuddy.navigation

import androidx.compose.runtime.Composable

class AppRoute(
    val mainName: String,
    val subRoutes: List<AppRoute> = listOf(),
    val startDestination: String = mainName,
    val screen: @Composable (() -> Unit)? = {},
    val icon: @Composable (() -> Unit)? = {},
)
