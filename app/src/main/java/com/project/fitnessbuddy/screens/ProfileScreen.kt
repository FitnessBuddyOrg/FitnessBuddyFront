package com.project.fitnessbuddy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.api.auth.AuthViewModel
import com.project.fitnessbuddy.api.auth.UserState
import com.project.fitnessbuddy.api.user.ProfileViewModel
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationViewModel

@Composable
fun ProfileScreen(
    userState: UserState,
    navController: NavHostController,
    navigationViewModel: NavigationViewModel,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    val user = profileViewModel.user.collectAsState()
    val isLoggedIn = userState.isLoggedIn

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            profileViewModel.fetchUser()
            name = user.value?.name ?: ""
        }
    }

    LaunchedEffect(Unit) {
        navigationViewModel.onEvent(NavigationEvent.SetTitle("Profile"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // User Info Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "User Profile",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Email:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = user.value?.email ?: stringResource(id = R.string.no_data),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Name:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.weight(1f)
                    )
                    if (isEditing) {
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .background(Color.LightGray, MaterialTheme.shapes.small)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            isEditing = false
                            user.value?.let {
                                profileViewModel.updateUser(it.id, name)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Save Changes",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Text(
                            text = user.value?.name ?: stringResource(id = R.string.no_data),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Name",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        IconButtonWithText(
            text = stringResource(id = R.string.logout),
            icon = Icons.Default.ExitToApp,
            onClick = {
                authViewModel.logout()
                profileViewModel.clearUserData()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun IconButtonWithText(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        modifier = Modifier.padding(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White)
    }
}

