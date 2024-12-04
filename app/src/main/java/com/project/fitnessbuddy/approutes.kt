package com.project.fitnessbuddy

import androidx.compose.ui.res.stringResource

class AppRoute (
    val text: String,
    val action: Runnable
)

val homeScreenRoute = stringResource(id = R.string.home)
val profileScreenRoute = stringResource(id = R.string.profile)

val appRoutes = listOf(homeScreenRoute, profileScreenRoute)
