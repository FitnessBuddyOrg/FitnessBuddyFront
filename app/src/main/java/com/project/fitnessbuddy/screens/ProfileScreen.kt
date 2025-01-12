package com.project.fitnessbuddy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.auth.AuthViewModel
import com.project.fitnessbuddy.auth.UserState
import com.project.fitnessbuddy.navigation.DefaultTitleWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(userState: UserState, authViewModel: AuthViewModel, navController: NavHostController, navigationViewModel: NavigationViewModel,) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val coroutineScope = remember {
        lifecycleOwner.lifecycleScope
    }

    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            navigationViewModel.onEvent(NavigationEvent.SetTitle(context.getString(R.string.profile)))
            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                DefaultTitleWidget(context.getString(R.string.profile))
            })
        }

        onDispose {
            job.cancel()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.welcome, userState.email ?: ""),
            style = MaterialTheme.typography.headlineMedium
        )

        Button(onClick = {
            authViewModel.logout()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }) {
            Text(stringResource(id = R.string.logout))
        }
    }
}
