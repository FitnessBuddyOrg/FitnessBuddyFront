package com.project.fitnessbuddy

import androidx.compose.runtime.Composable

class AppRoute(val name: String, val route: () -> Unit, val icon: @Composable (() -> Unit)?, val screen: @Composable (() -> Unit)?) {
}
