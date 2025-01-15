package com.project.fitnessbuddy.screens.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.auth.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val userState by authViewModel.userState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(id = R.string.login), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(id = R.string.email)) })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password)) },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                authViewModel.login(email, password)
            }) {
                Text(stringResource(id = R.string.login))
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {
                navController.navigate(context.getString(R.string.register_route))
            }) {
                Text(stringResource(id = R.string.dont_have_account))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                authViewModel.loginWithGoogle(context as Activity)
            }) {
                Text(text = "Sign in with Google")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                authViewModel.loginWithGitHub(context as Activity)
            }) {
                Text(text = "Sign in with GitHub")
            }


            if (userState.isLoggedIn) {
                onLoginSuccess()
            }
        }
    }
}
