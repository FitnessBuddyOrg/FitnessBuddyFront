package com.project.fitnessbuddy

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.fitnessbuddy.screens.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    val home = stringResource(id = R.string.home)
    val profile = stringResource(id = R.string.profile)
    val browseExercises = stringResource(id = R.string.browse_exercises)
    val browseRoutines = stringResource(id = R.string.browse_routines)
    val myExercises = stringResource(id = R.string.my_exercises)
    val myRoutines = stringResource(id = R.string.my_routines)
    val progressCalendar = stringResource(id = R.string.progress_calendar)
    val statistics = stringResource(id = R.string.statistics)

    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: home
    val navigationActions = remember(navController) {
        AppNavigationActions(navController)
    }

    val appRoutes = listOf(
        AppRoute(
            home,
            route = { navigationActions.navigateTo(home) },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
            screen = { homeScreen() }
        ),
        AppRoute(
            profile,
            route = { navigationActions.navigateTo(profile) },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
            screen = { profileScreen() }
        ),
        AppRoute(
            browseExercises,
            route = { navigationActions.navigateTo(browseExercises) },
            icon = { Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null) },
            screen = { browseExercisesScreen() }
        ),
        AppRoute(
            browseRoutines,
            route = { navigationActions.navigateTo(browseRoutines) },
            icon = { Icon(imageVector = Icons.Default.BrowseGallery, contentDescription = null) },
            screen = { browseRoutinesScreen() }
        ),
        AppRoute(
            myExercises,
            route = { navigationActions.navigateTo(myExercises) },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.exercise),
                    contentDescription = null
                )
            },
            screen = { myExercisesScreen() }
        ),
        AppRoute(
            myRoutines,
            route = { navigationActions.navigateTo(myRoutines) },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.routine),
                    contentDescription = null
                )
            },
            screen = { myRoutinesScreen() }
        ),
        AppRoute(
            progressCalendar,
            route = { navigationActions.navigateTo(progressCalendar) },
            icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null) },
            screen = { progressCalendarScreen() }
        ),
        AppRoute(
            statistics,
            route = { navigationActions.navigateTo(statistics) },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.monitoring),
                    contentDescription = null
                )
            },
            screen = { statisticsScreen() }
        ),
    )

    ModalNavigationDrawer(drawerContent = {
        AppDrawer(
            route = currentRoute,
            closeDrawer = { coroutineScope.launch { drawerState.close() } },
            appRoutes = appRoutes,
            modifier = Modifier
        )
    }, drawerState = drawerState) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = currentRoute) },
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch { drawerState.open() }
                        }, content = {
                            Icon(
                                imageVector = Icons.Default.Menu, contentDescription = null
                            )
                        })
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                )
            }, modifier = Modifier
        ) {
            NavHost(
                navController = navController, startDestination = home, modifier = modifier.padding(it)
            ) {
                appRoutes.forEach { appRoute ->
                    composable(appRoute.name) {
                        appRoute.screen?.let { it1 -> it1() }
                    }
                }
            }
        }
    }
}
