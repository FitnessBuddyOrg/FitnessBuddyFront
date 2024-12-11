package com.project.fitnessbuddy.screens

import androidx.navigation.NavHostController

//object AllDestinations {
//    val HOME = stringResource(id = R.string.home)
//    val PROFILE = stringResource(id = R.string.profile)
//}

class AppNavigationActions(private val navController: NavHostController) {

//    fun navigateToHome() {
//        navController.navigate(HOME) {
//            popUpTo(HOME)
//        }
//    }
//
//    fun navigateToProfile() {
//        navController.navigate(PROFILE) {
//            launchSingleTop = true
//            restoreState = true
//        }
//    }

    fun navigateTo(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    }
}
