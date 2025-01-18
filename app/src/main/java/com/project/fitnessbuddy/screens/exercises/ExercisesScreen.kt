package com.project.fitnessbuddy.screens.exercises

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.project.fitnessbuddy.screens.common.SelectedExerciseWidget
import kotlinx.coroutines.launch

@Composable
fun ExercisesScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,

    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel,

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
                MediumTextWidget(context.getString(R.string.exercises))
            })

            navigationViewModel.onEvent(NavigationEvent.AddTopBarActions {
                SearchButton(
                    title = stringResource(R.string.exercises),
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
            SelectedExerciseWidget(
                exercise = it,
                onClick = { exercise, _ ->
                    exercisesViewModel.onEvent(ExercisesEvent.SetSelectedExercise(exercise))
                    navigationState.navController?.navigate(context.getString(R.string.view_exercise_route))
                }
            )
        },
        parametersState = parametersState
    )
}
