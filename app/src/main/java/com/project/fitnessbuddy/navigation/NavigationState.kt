package com.project.fitnessbuddy.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

data class NavigationState(
    var navController: NavController? = null,

    var title: String = "",
    var titleWidget: @Composable () -> Unit = {},

    var topBarActions: List<@Composable RowScope.() -> Unit> = emptyList(),
    var backButtonEnabled: Boolean = false
)
