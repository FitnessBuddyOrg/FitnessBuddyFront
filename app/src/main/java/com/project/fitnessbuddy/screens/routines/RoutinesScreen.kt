package com.project.fitnessbuddy.screens.routines

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.project.fitnessbuddy.navigation.CreateButton
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.navigation.SearchButton
import com.project.fitnessbuddy.screens.common.GroupedWidgetList
import com.project.fitnessbuddy.screens.common.ParametersState
import com.project.fitnessbuddy.screens.common.ParametersViewModel
import com.project.fitnessbuddy.screens.common.timeAgo
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
            navigationViewModel.onEvent(NavigationEvent.ClearTopBarActions)
            navigationViewModel.onEvent(NavigationEvent.DisableCustomButton)

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                MediumTextWidget(context.getString(R.string.routines))
            })

            navigationViewModel.onEvent(NavigationEvent.AddTopBarActions {
                SearchButton(
                    title = stringResource(R.string.routines),
                    navigationViewModel = navigationViewModel,
                    onValueChange = {
                        routinesViewModel.onEvent(RoutinesEvent.SetSearchValue(it))
                    },
                    onClear = {
                        routinesViewModel.onEvent(RoutinesEvent.SetSearchValue(""))
                    }
                )
                CreateButton(
                    onClick = {
                        routinesViewModel.onEvent(RoutinesEvent.SetEditType(EditType.ADD))
                        routinesViewModel.onEvent(RoutinesEvent.SetSelectedRoutineDTO(RoutineDTO()))
                        navigationState.navController?.navigate(context.getString(R.string.add_edit_routine_route))
                    }
                )
            })
        }

        onDispose {
            job.cancel()
        }
    }

    GroupedWidgetList(
        itemsList = routinesState.templateRoutineDTOs,
        widget = @Composable {
            RoutineWidget(
                routineDTO = it,
                navigationState = navigationState,
                routinesViewModel = routinesViewModel
            )
        },
        parametersState = parametersState,
        verticalArrangement = Arrangement.spacedBy(8.dp),

        keySelector = { it.name.first().uppercase() }
    )
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
                    routinesViewModel.onEvent(RoutinesEvent.SetSelectedRoutineDTO(routineDTO))
                    navigationState.navController?.navigate(context.getString(R.string.view_routine_route))
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = {
                routinesViewModel.onEvent(RoutinesEvent.SetEditType(EditType.EDIT))
                routinesViewModel.onEvent(RoutinesEvent.SetSelectedRoutineDTO(routineDTO))
                navigationState.navController?.navigate(context.getString(R.string.add_edit_routine_route))
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

            if (routineDTO.routine.lastPerformed != null) {
                Text(
                    text = "${stringResource(R.string.last_performed)}: ${routineDTO.routine.lastPerformed?.timeAgo(context)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            routineDTO.routineExerciseDTOs.forEach { routineExerciseDTO ->
                Text(
                    text = "${routineExerciseDTO.routineExerciseSetDTOs.size} × ${routineExerciseDTO.exercise.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


