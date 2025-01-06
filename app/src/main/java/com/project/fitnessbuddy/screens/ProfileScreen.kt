package com.project.fitnessbuddy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.auth.AuthViewModel
import com.project.fitnessbuddy.auth.UserState

@Composable
fun ProfileScreen(userState: UserState, authViewModel: AuthViewModel, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.welcome, userState.email ?: ""), style = MaterialTheme.typography.headlineMedium)

        Button(onClick = {
            authViewModel.logout()
            navController.navigate("login") {
                popUpTo(0)
            }
        }) {
            Text(stringResource(id = R.string.logout))
        }
    }
}