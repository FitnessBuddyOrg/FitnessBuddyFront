package com.project.fitnessbuddy.screens.routines

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.ParametersState
import com.project.fitnessbuddy.screens.common.ParametersViewModel
import com.project.fitnessbuddy.screens.exercises.ExercisesEvent
import kotlinx.coroutines.launch

@Composable
fun RoutinesScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,

    routinesState: RoutinesState,
    routinesViewModel: RoutinesViewModel,

    parametersState: ParametersState,
    parametersViewModel: ParametersViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = remember {
        lifecycleOwner.lifecycleScope
    }

    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            navigationViewModel.onEvent(NavigationEvent.DisableAllButtons)
            navigationViewModel.onEvent(NavigationEvent.EnableSearchButton)
            navigationViewModel.onEvent(NavigationEvent.EnableAddButton)

            navigationViewModel.onEvent(NavigationEvent.SetAddButtonRoute(context.getString(R.string.add_edit_routine_route)))
            navigationViewModel.onEvent(NavigationEvent.SetEditButtonRoute(context.getString(R.string.add_edit_routine_route)))

            navigationViewModel.onEvent(NavigationEvent.SetTitle(context.getString(R.string.routines)))
            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                MediumTextWidget(context.getString(R.string.routines))
            })
        }

        onDispose {
            job.cancel()
        }
    }


    AlphabeticallyGroupedWidgetList(
        routinesState = routinesState,
        routinesViewModel = routinesViewModel,
        navigationState = navigationState,
        parametersState = parametersState
    )
}

@Composable
fun AlphabeticallyGroupedWidgetList(
    routinesState: RoutinesState,
    routinesViewModel: RoutinesViewModel,
    navigationState: NavigationState,
    parametersState: ParametersState
) {
    val context = LocalContext.current


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            routinesState.routineDTOs
                .filter { (it.routine.language.name == parametersState.languageParameter.value) || it.routine.language.isCustom }
                .groupBy {
                    it.routine.name.first().uppercase()
                }
                .toSortedMap()
                .forEach { (letter, routineDTOsInGroup) ->
                    item {
                        Text(
                            text = letter.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
                        )
                    }
                    items(routineDTOsInGroup) { routineDTO ->
                        RoutineWidget(
                            routineDTO = routineDTO,
                            navigationState = navigationState,
                            routinesViewModel = routinesViewModel
                        )
                    }
                }
        }
    }
}

@Composable
fun RoutineWidget(
    routineDTO: RoutineDTO,
    navigationState: NavigationState,
    routinesViewModel: RoutinesViewModel
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(8.dp))
            .clickable(
                onClick = {

                }
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = {
                routinesViewModel.onEvent(RoutinesEvent.SetEditType(EditType.EDIT))
                navigationState.navController?.navigate(navigationState.editButtonRoute)
            }
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = routineDTO.routine.name,
                style = MaterialTheme.typography.labelMedium
            )

            if (routineDTO.getLastPerformed().isNotEmpty()) {
                Text(
                    text = "${stringResource(R.string.last_performed)}: ${routineDTO.routine.lastPerformed}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            routineDTO.routineExerciseDTOs.forEach { routineExerciseDTO ->
                Text(
                    text = "${routineExerciseDTO.routineExerciseSets.size} x ${routineExerciseDTO.exercise.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

//@Composable
//fun ThreeDotsMenu(modifier: Modifier = Modifier) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = modifier
//    ) {
//        IconButton(onClick = { expanded = true }) {
//            Icon(
//                imageVector = Icons.Default.MoreVert,
//                contentDescription = "More Options"
//            )
//        }
//
//        DropdownMenu(
//            modifier = Modifier,
//            shape = RoundedCornerShape(8.dp),
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            DropdownMenuItem(
//                text = { Text(
//                    text = "Option 1",
//                    style = MaterialTheme.typography.labelMedium
//                ) },
//                onClick = {
//                    expanded = false
//                }
//            )
//            DropdownMenuItem(
//                text = { Text(
//                    text = "Option 2",
//                    style = MaterialTheme.typography.labelMedium
//                ) },
//                onClick = {
//                    expanded = false
//                }
//            )
//            DropdownMenuItem(
//                text = { Text(
//                    text = "Option 3",
//                    style = MaterialTheme.typography.labelMedium
//                ) },
//                onClick = {
//                    expanded = false
//                }
//            )
//        }
//    }
//}
