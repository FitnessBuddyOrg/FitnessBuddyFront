package com.project.fitnessbuddy.screens.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.navigation.CreateButton
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.navigation.SearchButton
import com.project.fitnessbuddy.screens.common.AlphabeticallyGroupedWidgetList
import com.project.fitnessbuddy.screens.common.ParametersState
import com.project.fitnessbuddy.screens.common.ParametersViewModel
import com.project.fitnessbuddy.screens.common.ValidationFloatingActionButton
import com.project.fitnessbuddy.screens.common.WidgetLetterImage
import com.project.fitnessbuddy.screens.exercises.ExercisesEvent
import com.project.fitnessbuddy.screens.exercises.ExercisesState
import com.project.fitnessbuddy.screens.exercises.ExercisesViewModel
import kotlinx.coroutines.launch

@Composable
fun AddExercisesScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,

    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel,

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

            navigationViewModel.onEvent(NavigationEvent.EnableCustomButton)
            navigationViewModel.onEvent(NavigationEvent.SetBackButton(
                navController = navigationState.navController,
                onClick = {
                    routinesViewModel.onEvent(RoutinesEvent.ClearExercisesLists)
                }
            ))

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                MediumTextWidget(context.getString(R.string.add_exercises))
            })

            navigationViewModel.onEvent(NavigationEvent.AddTopBarActions {
                SearchButton(
                    title = stringResource(R.string.add_exercises),
                    navigationViewModel = navigationViewModel,
                    onValueChange = {
                        exercisesViewModel.onEvent(ExercisesEvent.SetSearchValue(it))
                    },
                    onClear = {
                        exercisesViewModel.onEvent(ExercisesEvent.SetSearchValue(""))
                    }
                )
                CreateButton(
                    onClick = {
                        exercisesViewModel.onEvent(ExercisesEvent.SetSelectedExercise(Exercise()))
                        navigationState.navController?.navigate(context.getString(R.string.add_edit_exercise_route))
                    }
                )
            })
        }

        onDispose {
            job.cancel()
        }
    }
    fun isSelected(exercise: Exercise): Boolean {
        val routineExerciseDTO =
            routinesState.selectedRoutineDTO.routineExerciseDTOs.find {
                it.routineExercise.exerciseId == exercise.exerciseId &&
                it.routineExercise.routineId == routinesState.selectedRoutineDTO.routine.routineId
            }
        return routineExerciseDTO != null
    }


    AlphabeticallyGroupedWidgetList(
        sortingState = exercisesState,
        itemsList = exercisesState.exercises,
        onClick = {
            exercisesViewModel.onEvent(
                ExercisesEvent.SortExercises(
                    it.value
                )
            )
        },
        widget = @Composable {
            ExerciseWidget(
                exercise = it,
                onClick = { exercise, selected ->
                    routinesViewModel.onEvent(RoutinesEvent.HandleExercise(exercise, selected))
                },
                initialSelected = isSelected(it)
            )
        },
        floatingActionButton = {
            ValidationFloatingActionButton(
                context = context,
                onClick = ({
                    routinesViewModel.onEvent(RoutinesEvent.ApplyExercises)
                    navigationState.navController?.navigateUp()
                    false
                }),
                toasting = false
            )
        },
        parametersState = parametersState
    )
}

@Composable
fun ExerciseWidget(
    exercise: Exercise,
    onClick: (Exercise, Boolean) -> Unit,
    initialSelected: Boolean,
) {
    val colorScheme = MaterialTheme.colorScheme
    var selected by remember { mutableStateOf(initialSelected) }
    fun setBackgroundColor(): Color {
        return if (selected) {
            colorScheme.onPrimary
        } else {
            Color.Transparent
        }
    }
    var backgroundColor by remember { mutableStateOf(setBackgroundColor()) }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(backgroundColor)
            .clickable(
                onClick = {
                    selected = !selected
                    backgroundColor = setBackgroundColor()
                    onClick(exercise, selected)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WidgetLetterImage(
            letter = exercise.name.first(),
            padding = PaddingValues(start = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = exercise.name,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(exercise.category.resourceId),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


