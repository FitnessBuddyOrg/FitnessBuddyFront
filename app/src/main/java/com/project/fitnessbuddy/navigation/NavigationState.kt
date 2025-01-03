package com.project.fitnessbuddy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

data class NavigationState(
    var navController: NavController? = null,

    var title: String = "",
    var titleWidget: @Composable () -> Unit = {},

//    var searchValue: String = "",

    var backButtonEnabled: Boolean = false,
    var searchButtonEnabled: Boolean = false,

    var addButtonEnabled: Boolean = false,
    var addButtonRoute: String = "",

    var deleteButtonEnabled: Boolean = false,
    var onDeleteButtonClicked: (() -> Unit)? = null,

    var editButtonEnabled: Boolean = false,
    var editButtonRoute: String = "",

)
