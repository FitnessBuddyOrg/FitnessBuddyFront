package com.project.fitnessbuddy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.fitnessbuddy.api.auth.UserState
import com.project.fitnessbuddy.api.user.ProfileViewModel
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationViewModel

@Composable
fun ProfileScreen(
    userState: UserState,
    navController: NavHostController,
    navigationViewModel: NavigationViewModel,
    profileViewModel: ProfileViewModel
) {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(userState.name ?: "") }

    val user = profileViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        navigationViewModel.onEvent(NavigationEvent.SetTitle("Profile"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "User Information",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Email: ${user.value?.email ?: "Loading..."}")
                Spacer(modifier = Modifier.height(8.dp))

                if (isEditing) {
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray, MaterialTheme.shapes.small)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            isEditing = false
                            name = user.value?.name ?: ""
                        }) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            isEditing = false
                            user.value?.let {
                                profileViewModel.updateUser(it.id, name)
                            }
                        }) {
                            Text("Save")
                        }
                    }
                } else {
                    Text(text = "Name: ${user.value?.name ?: "Loading..."}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { isEditing = true }) {
                        Text("Edit Name")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }) {
            Text("Logout")
        }
    }
}
