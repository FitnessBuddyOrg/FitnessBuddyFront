package com.project.fitnessbuddy.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.auth.UserState

@Composable
fun AppDrawer(
    route: String,
    modifier: Modifier = Modifier,
    appRoutes: List<AppRoute> = listOf(),
    navController: NavController,
    userState: UserState,
    closeDrawer: () -> Unit = {}
) {
    ModalDrawerSheet(modifier = Modifier) {
        DrawerHeader(modifier, userState)
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.spacer_padding)))

        appRoutes.forEach { appRoute ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = if (appRoute.subRoutes.isNotEmpty()) appRoute.subRoutes[0].mainName else appRoute.mainName,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = route == appRoute.mainName,
                onClick = {
                    navController.navigate(appRoute.mainName) {
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
fun DrawerHeader(modifier: Modifier, userState: UserState) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondary)
            .padding(dimensionResource(id = R.dimen.header_padding))
            .fillMaxWidth()
    ) {

        Image(
            painterResource(id = R.drawable.profile_picture),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(dimensionResource(id = R.dimen.header_image_size))
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.spacer_padding)))

        Text(
            text = userState.email ?: stringResource(id = R.string.username_placeholder),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}