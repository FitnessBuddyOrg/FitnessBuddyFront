package com.project.fitnessbuddy.navigation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.api.auth.AuthViewModel
import com.project.fitnessbuddy.api.auth.UserState
import kotlinx.coroutines.launch


@Composable
fun AppDrawer(
    route: String,
    modifier: Modifier = Modifier,
    appRoutes: List<AppRoute> = listOf(),
    navController: NavController,
    userState: UserState,
    closeDrawer: () -> Unit = {},
    authViewModel: AuthViewModel
) {
    ModalDrawerSheet(modifier = Modifier) {
        DrawerHeader(modifier, userState, authViewModel)
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.spacer_padding)))

        appRoutes.forEach { appRoute ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = if (appRoute.subRoutes.isNotEmpty()) appRoute.subRoutes[0].name else appRoute.name,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = route == appRoute.routeName,
                onClick = {
                    navController.navigate(appRoute.routeName) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(route) {
                            inclusive = true
                        }
                    }
                    closeDrawer()
                },
                icon = appRoute.icon,
                shape = MaterialTheme.shapes.small
            )
        }
    }
}

@Composable
fun DrawerHeader(modifier: Modifier, userState: UserState, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    var profilePictureUrl by remember { mutableStateOf<String?>(null) }
    val loading by authViewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        if (userState.isLoggedIn) {
            authViewModel.viewModelScope.launch {
                try {
                    profilePictureUrl = RetrofitInstance.userApi.getProfilePicture().url
                } catch (e: Exception) {
                    Log.e("DrawerHeader", "Failed to fetch profile picture: ${e.localizedMessage}")
                    profilePictureUrl = null
                }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondary)
            .padding(dimensionResource(id = R.dimen.header_padding))
            .fillMaxWidth()
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        } else {
            Image(

                painter = if (profilePictureUrl.isNullOrEmpty()) {
                    painterResource(id = R.drawable.profile_picture)
                } else {
                    rememberAsyncImagePainter(profilePictureUrl)
                },
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.header_image_size))
                    .clip(CircleShape)
            )

        }

        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.spacer_padding)))

        Text(
            text = userState.email ?: stringResource(id = R.string.username_placeholder),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}
