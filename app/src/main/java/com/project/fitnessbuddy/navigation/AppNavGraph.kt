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
import com.project.fitnessbuddy.screens.ProfileScreen
import com.project.fitnessbuddy.screens.ProgressCalendarScreen
import com.project.fitnessbuddy.screens.routines.RoutinesScreen
import com.project.fitnessbuddy.screens.StatisticsScreen
import com.project.fitnessbuddy.screens.auth.LoginScreen
import com.project.fitnessbuddy.screens.auth.RegisterScreen
import com.project.fitnessbuddy.screens.exercises.AddEditExerciseScreen
import com.project.fitnessbuddy.screens.exercises.ExercisesScreen
import com.project.fitnessbuddy.screens.exercises.ExercisesState
import com.project.fitnessbuddy.screens.exercises.ExercisesViewModel
import com.project.fitnessbuddy.screens.exercises.ViewExerciseScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navigationState: NavigationState,
    exercisesState: ExercisesState,
    navigationViewModel: NavigationViewModel,
    exercisesViewModel: ExercisesViewModel,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    statisticsViewModel: StatisticsViewModel,
    userState: UserState,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    navigationViewModel.onEvent(NavigationEvent.SetNavController(navController))

    val home = stringResource(id = R.string.home)
    val profile = stringResource(id = R.string.profile)
    val exercises = stringResource(id = R.string.exercises)
    val exercisesOverview = stringResource(id = R.string.exercises_overview)
    val addEditExercise = stringResource(id = R.string.add_edit_exercise)
    val viewExercise = stringResource(id = R.string.view_exercise)
    val routines = stringResource(id = R.string.routines)
    val progressCalendar = stringResource(id = R.string.progress_calendar)
    val statistics = stringResource(id = R.string.statistics)

    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: home

    val appRoutes: List<AppRoute> = listOf(
        AppRoute(
            mainName = home,
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
            screen = { HomeScreen(
                navController = navController
            ) }
        ),
        AppRoute(
            mainName = profile,
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
            screen = { ProfileScreen(
                userState = userState,
                navController = navController,
                navigationViewModel = navigationViewModel,
                profileViewModel = profileViewModel,
                authViewModel = authViewModel
            ) }
        ),
        AppRoute(
            mainName = exercisesOverview,
            startDestination = exercises,
            icon = { Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null) },
            subRoutes = listOf(
                AppRoute(
                    mainName = exercises,
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
                    mainName = addEditExercise,
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
                    mainName = viewExercise,
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
            mainName = routines,
            icon = { Icon(imageVector = Icons.Default.BrowseGallery, contentDescription = null) },
            screen = { RoutinesScreen() }
        ),
        AppRoute(
            mainName = progressCalendar,
            icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null) },
            screen = { ProgressCalendarScreen() }
        ),
        AppRoute(
            mainName = statistics,
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.monitoring),
                    contentDescription = null
                )
            },
            screen = { StatisticsScreen(
                statisticsViewModel = statisticsViewModel,
                userState = userState
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
                userState = userState
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
                    startDestination = if (userState.isLoggedIn) "home" else "login",
                    modifier = modifier.padding(it)
                ) {
                    composable("login") {
                        LoginScreen(
                            navController = navController,
                            authViewModel = authViewModel,
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            navController = navController,
                            authViewModel = authViewModel,
                            onRegisterSuccess = {
                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            navController = navController
                        )
                    }

                    composable("progressCalendar") {
                        ProgressCalendarScreen()
                    }

                    appRoutes.forEach { appRoute ->
                        if (appRoute.subRoutes.isNotEmpty()) {
                            navigation(
                                startDestination = appRoute.startDestination,
                                route = appRoute.mainName
                            ) {
                                appRoute.subRoutes.forEach { subRoute ->
                                    composable(subRoute.mainName) {
                                        subRoute.screen?.let { it1 -> it1() }
                                    }
                                }
                            }
                        } else {
                            composable(appRoute.mainName) {
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
            startDestination = "login",
            modifier = modifier
        ) {
            composable("login") {
                LoginScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}