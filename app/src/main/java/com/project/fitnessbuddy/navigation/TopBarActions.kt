package com.project.fitnessbuddy.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.screens.exercises.ExercisesEvent
import com.project.fitnessbuddy.screens.exercises.ExercisesState
import com.project.fitnessbuddy.screens.exercises.ExercisesViewModel

@Composable
fun MoreVertButton() {
    IconButton(onClick = {}) {
        Icon(Icons.Default.MoreVert, contentDescription = "More")
    }
}

@Composable
fun SearchButton(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,
    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel
) {
    var isSearchEnabled by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
        if (isSearchEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(4.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
//                        navigationViewModel.onEvent(NavigationEvent.SetSearchValue(it))
                        exercisesViewModel.onEvent(ExercisesEvent.SetSearchValue(it))
                        searchText = it
                    },
                    placeholder = {
                        Text(
                            text = "Search...",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(0.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .focusRequester(focusRequester),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        IconButton(onClick = {
//                            navigationViewModel.onEvent(NavigationEvent.SetSearchValue(""))
                            exercisesViewModel.onEvent(ExercisesEvent.SetSearchValue(""))
                            searchText = ""
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Search")
                        }
                    }
                )
            }

            LaunchedEffect(isSearchEnabled) {
                if (isSearchEnabled) {
                    focusRequester.requestFocus()
                }
            }
        } else {
            DefaultTitleWidget(navigationState.title)
        }
    })

    if (isSearchEnabled) {
        IconButton(onClick = {
            isSearchEnabled = false
        }) {
            Icon(Icons.Default.Close, contentDescription = "Close Search")
        }
    } else {
        IconButton(
            onClick = {
                isSearchEnabled = true
            }
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
    }
}

@Composable
fun CreateButton(navigationState: NavigationState, exercisesViewModel: ExercisesViewModel) {
    IconButton(onClick = {
        exercisesViewModel.onEvent(ExercisesEvent.SetEditType(EditType.ADD))
        navigationState.navController?.navigate(navigationState.addButtonRoute)
    }) {
        Icon(Icons.Default.Add, contentDescription = "Create")
    }
}

@Composable
fun DeleteButton(navigationState: NavigationState) {
    IconButton(onClick = {
        navigationState.onDeleteButtonClicked?.invoke()
    }) {
        Icon(Icons.Default.Delete, contentDescription = "Delete")
    }
}

@Composable
fun EditButton(navigationState: NavigationState, exercisesViewModel: ExercisesViewModel) {
    IconButton(onClick = {
        exercisesViewModel.onEvent(ExercisesEvent.SetEditType(EditType.EDIT))
        navigationState.navController?.navigate(navigationState.editButtonRoute)
    }) {
        Icon(Icons.Default.Edit, contentDescription = "Edit")
    }
}

@Composable
fun DefaultTitleWidget(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium
    )
}

