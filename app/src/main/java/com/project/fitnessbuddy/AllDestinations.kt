package com.project.fitnessbuddy

import androidx.navigation.NavHostController
import com.project.fitnessbuddy.AllDestinations.HOME
import com.project.fitnessbuddy.AllDestinations.PROFILE

object AllDestinations {
    const val HOME = "Home"
    const val PROFILE = "Profile"
}

class AppNavigationActions(private val navController: NavHostController) {

    fun navigateToHome() {
        navController.navigate(HOME) {
            popUpTo(HOME)
        }
    }

    fun navigateToProfile() {
        navController.navigate(PROFILE) {
            launchSingleTop = true
            restoreState = true
        }
    }
}
