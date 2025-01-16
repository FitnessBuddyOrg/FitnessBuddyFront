package com.project.fitnessbuddy.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

sealed interface NavigationEvent {
    data class SetNavController(val navController: NavController) : NavigationEvent

    data class SetTitle(val title: String) : NavigationEvent
    data class UpdateTitleWidget(val titleWidget: @Composable () -> Unit) : NavigationEvent

    data class AddTopBarActions(val topBarActions: @Composable RowScope.() -> Unit) : NavigationEvent
    data object ClearTopBarActions : NavigationEvent

    data object DisableAllButtons : NavigationEvent

    data object EnableBackButton : NavigationEvent
}
