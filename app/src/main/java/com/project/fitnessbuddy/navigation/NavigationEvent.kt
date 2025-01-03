package com.project.fitnessbuddy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

sealed interface NavigationEvent {
    data class SetNavController(val navController: NavController) : NavigationEvent

    data class SetTitle(val title: String) : NavigationEvent
    data class UpdateTitleWidget(val titleWidget: @Composable () -> Unit) : NavigationEvent

//    data class SetSearchValue(val searchValue: String) : NavigationEvent
//    data object ClearSearchValue : NavigationEvent

    data object DisableAllButtons : NavigationEvent

    data object EnableBackButton : NavigationEvent
    data object DisableBackButton : NavigationEvent

    data object EnableSearchButton: NavigationEvent
    data object DisableSearchButton: NavigationEvent

    data object EnableAddButton: NavigationEvent
    data object DisableAddButton: NavigationEvent
    data class SetAddButtonRoute(val addButtonRoute: String): NavigationEvent

    data object EnableDeleteButton: NavigationEvent
    data object DisableDeleteButton: NavigationEvent
    data class SetOnDeleteButtonClicked(val onDeleteButtonClicked: () -> Unit): NavigationEvent

    data object EnableEditButton: NavigationEvent
    data object DisableEditButton: NavigationEvent
    data class SetEditButtonRoute(val editButtonRoute: String): NavigationEvent
}
