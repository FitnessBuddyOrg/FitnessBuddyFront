package com.project.fitnessbuddy.navigation


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.api.auth.AuthViewModel
import com.project.fitnessbuddy.api.auth.UserState
import com.project.fitnessbuddy.api.statistics.StatisticsViewModel
import com.project.fitnessbuddy.api.user.ProfileViewModel
import com.project.fitnessbuddy.screens.HomeScreen
import com.project.fitnessbuddy.screens.profile.ProfileScreen
import com.project.fitnessbuddy.screens.ProgressCalendarScreen
import com.project.fitnessbuddy.screens.StatisticsScreen
import com.project.fitnessbuddy.screens.auth.LoginScreen
import com.project.fitnessbuddy.screens.auth.RegisterScreen
import com.project.fitnessbuddy.screens.common.ParametersEvent
import com.project.fitnessbuddy.screens.common.ParametersState
import com.project.fitnessbuddy.screens.common.ParametersViewModel
import com.project.fitnessbuddy.screens.exercises.AddEditExerciseScreen
import com.project.fitnessbuddy.screens.exercises.ExercisesScreen
import com.project.fitnessbuddy.screens.exercises.ExercisesState
import com.project.fitnessbuddy.screens.exercises.ExercisesViewModel
import com.project.fitnessbuddy.screens.exercises.ViewExerciseScreen
import com.project.fitnessbuddy.screens.profile.ProfileScreen
import com.project.fitnessbuddy.screens.routines.RoutinesScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,

    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,

    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel,

    parametersState: ParametersState,
    parametersViewModel: ParametersViewModel,

    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    statisticsViewModel: StatisticsViewModel,
    userState: UserState,

    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    navigationViewModel.onEvent(NavigationEvent.SetNavController(navController))
    parametersViewModel.onEvent(ParametersEvent.InitializeParameters)

    // TODO - REMOVE THIS FOR PRODUCTION
    val bypassLogin = false

    val loginRoute = stringResource(id = R.string.login_route)
    val registerRoute = stringResource(id = R.string.register_route)

    val homeRoute = stringResource(id = R.string.home_route)
    val profileRoute = stringResource(id = R.string.profile_route)

    val exercisesRoute = stringResource(id = R.string.exercises_route)
    val exercisesOverviewRoute = stringResource(id = R.string.exercises_overview_route)
    val addEditExerciseRoute = stringResource(id = R.string.add_edit_exercise_route)
    val viewExerciseRoute = stringResource(id = R.string.view_exercise_route)

    val routinesRoute = stringResource(id = R.string.routines_route)
    val progressCalendarRoute = stringResource(id = R.string.progress_calendar_route)
    val statisticsRoute = stringResource(id = R.string.statistics_route)

    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: homeRoute

    val appRoutes: List<AppRoute> = listOf(
        AppRoute(
            routeName = homeRoute,
            name = stringResource(id = R.string.home),
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
            screen = { HomeScreen(
                navController = navController,
                navigationViewModel = navigationViewModel
            ) }
        ),
        AppRoute(
            routeName = profileRoute,
            name = stringResource(id = R.string.profile),
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
            screen = { ProfileScreen(
                userState = userState,
                navController = navController,
                navigationViewModel = navigationViewModel,
                profileViewModel = profileViewModel,
                authViewModel = authViewModel,
                parametersState = parametersState,
                parametersViewModel = parametersViewModel,
            ) }
        ),
        AppRoute(
            routeName = exercisesOverviewRoute,
            name = stringResource(id = R.string.exercises_overview),
            startDestination = exercisesRoute,
            icon = { Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null) },
            subRoutes = listOf(
                AppRoute(
                    routeName = exercisesRoute,
                    name = stringResource(id = R.string.exercises),
                    screen = {
                        ExercisesScreen(
                            navigationState = navigationState,
                            navigationViewModel = navigationViewModel,
                            exercisesState = exercisesState,
                            exercisesViewModel = exercisesViewModel,
                        )
                    }
                ),
                AppRoute(
                    routeName = addEditExerciseRoute,
                    name = stringResource(id = R.string.add_edit_exercise),
                    screen = {
                        AddEditExerciseScreen(
                            navigationState = navigationState,
                            navigationViewModel = navigationViewModel,
                            exercisesState = exercisesState,
                            exercisesViewModel = exercisesViewModel,
                        )
                    }
                ),
                AppRoute(
                    routeName = viewExerciseRoute,
                    name = stringResource(id = R.string.view_exercise),
                    screen = {
                        ViewExerciseScreen(
                            navigationState = navigationState,
                            navigationViewModel = navigationViewModel,
                            exercisesState = exercisesState,
                            exercisesViewModel = exercisesViewModel,
                        )
                    }
                )
            )
        ),
        AppRoute(
            routeName = routinesRoute,
            name = stringResource(id = R.string.routines),
            icon = { Icon(imageVector = Icons.Default.BrowseGallery, contentDescription = null) },
            screen = { RoutinesScreen() }
        ),
        AppRoute(
            routeName = progressCalendarRoute,
            name = stringResource(id = R.string.progress_calendar),
            icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null) },
            screen = { ProgressCalendarScreen() }
        ),
        AppRoute(
            routeName = statisticsRoute,
            name = stringResource(id = R.string.statistics),
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.monitoring),
                    contentDescription = null
                )
            },
            screen = { StatisticsScreen(
                statisticsViewModel = statisticsViewModel,
                userState = userState,
                navigationViewModel = navigationViewModel
            ) }
        ),
    )

    if (userState.isLoggedIn) {
        ModalNavigationDrawer(drawerContent = {
            AppDrawer(
                route = currentRoute,
                closeDrawer = { coroutineScope.launch { drawerState.close() } },
                appRoutes = appRoutes,
                navController = navController,
                modifier = Modifier,
                userState = userState,
                authViewModel = authViewModel,
            )
        }, drawerState = drawerState) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = navigationState.titleWidget,
                        modifier = Modifier.fillMaxWidth(),
                        navigationIcon = {
                            if (navigationState.backButtonEnabled) {
                                IconButton(onClick = {
                                    navController.navigateUp()
                                }, content = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null
                                    )
                                })
                            } else {
                                IconButton(onClick = {
                                    coroutineScope.launch { drawerState.open() }
                                }, content = {
                                    Icon(
                                        imageVector = Icons.Default.Menu, contentDescription = null
                                    )
                                })
                            }
                        },
                        actions = {
                            if (navigationState.searchButtonEnabled) {
                                SearchButton(
                                    navigationState = navigationState,
                                    navigationViewModel = navigationViewModel,
                                    exercisesState = exercisesState,
                                    exercisesViewModel = exercisesViewModel,
                                )
                            }
                            if (navigationState.addButtonEnabled) {
                                CreateButton(navigationState, exercisesViewModel)
                            }
                            if (navigationState.editButtonEnabled) {
                                EditButton(navigationState, exercisesViewModel)
                            }
                            if (navigationState.deleteButtonEnabled) {
                                DeleteButton(navigationState)
                            }
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }, modifier = Modifier
            ) {
                NavHost(
                    navController = navController,
                    startDestination = if (userState.isLoggedIn || bypassLogin) homeRoute else loginRoute,
                    modifier = modifier.padding(it)
                ) {
                    composable(loginRoute) {
                        LoginScreen(
                            navController = navController,
                            authViewModel = authViewModel,
                            onLoginSuccess = {
                                navController.navigate(homeRoute) {
                                    popUpTo(loginRoute) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(registerRoute) {
                        RegisterScreen(
                            navController = navController,
                            authViewModel = authViewModel,
                            onRegisterSuccess = {
                                navController.navigate(homeRoute) {
                                    popUpTo(registerRoute) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            navController = navController,
                            navigationViewModel = navigationViewModel
                        )
                    }

                    composable("progressCalendar") {
                        ProgressCalendarScreen()
                    }

                    appRoutes.forEach { appRoute ->
                        if (appRoute.subRoutes.isNotEmpty()) {
                            navigation(
                                startDestination = appRoute.startDestination,
                                route = appRoute.routeName
                            ) {
                                appRoute.subRoutes.forEach { subRoute ->
                                    composable(subRoute.routeName) {
                                        subRoute.screen?.let { it1 -> it1() }
                                    }
                                }
                            }
                        } else {
                            composable(appRoute.routeName) {
                                appRoute.screen?.let { it1 -> it1() }
                            }
                        }
                    }
                }
            }
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = loginRoute,
            modifier = modifier
        ) {
            composable(loginRoute) {
                LoginScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(homeRoute) {
                            popUpTo(loginRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable(registerRoute) {
                RegisterScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(homeRoute) {
                            popUpTo(registerRoute) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}