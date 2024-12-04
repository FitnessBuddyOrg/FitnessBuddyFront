package com.project.fitnessbuddy.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.fitnessbuddy.appRoutes
import com.project.fitnessbuddy.homeScreenRoute
import com.project.fitnessbuddy.profileScreenRoute
import com.project.fitnessbuddy.screens.homeScreen
import com.project.fitnessbuddy.screens.profileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun drawerActivity() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            drawerContent(navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("Empty Drawer Example") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            content = {
                NavHost(navController = navController, startDestination = homeScreenRoute) {
                    composable(homeScreenRoute) {
                        homeScreen()
                    }
                    composable(profileScreenRoute) {
                        profileScreen()
                    }
                }
            }
        )
    }
}

@Composable
fun drawerContent(navController: NavController) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.7f

    Column(
        modifier = Modifier
            .width(drawerWidth)
            .fillMaxHeight()
            .background(Color.White)
    ) {
        appRoutes.forEach { appRoute ->
            drawerComponent(navController, appRoute)
        }
    }
}

@Composable
fun drawerComponent(navController: NavController, appRoute: String) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(
        if (appRoute == homeScreenRoute) DrawerValue.Open else DrawerValue.Closed
    )

    Button(
        onClick = {
            navController.navigate(appRoute)
            scope.launch { drawerState.open() }
        }
    ) {
        Text(
            text = appRoute,
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            color = Color.Black
        )
    }
}
