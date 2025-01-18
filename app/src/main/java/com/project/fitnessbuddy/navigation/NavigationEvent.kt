package com.project.fitnessbuddy.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

sealed interface NavigationEvent {
    data class SetNavController(val navController: NavController) : NavigationEvent

    data class UpdateTitleWidget(val titleWidget: @Composable () -> Unit) : NavigationEvent

    data class AddTopBarActions(val topBarActions: @Composable RowScope.() -> Unit) : NavigationEvent
    data object ClearTopBarActions : NavigationEvent

    data class SetBackButton(val navController: NavController?, val onClick: () -> Unit = {}) : NavigationEvent

    data object DisableCustomButton : NavigationEvent

    data object EnableCustomButton : NavigationEvent
}
