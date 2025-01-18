package com.project.fitnessbuddy.screens.exercises

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.project.fitnessbuddy.screens.common.WidgetLetterImage
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
            ExerciseWidget(
                exercise = it,
                onClick = { exercise ->
                    exercisesViewModel.onEvent(ExercisesEvent.SetSelectedExercise(exercise))
                    navigationState.navController?.navigate(context.getString(R.string.view_exercise_route))
                }
            )
        },
        parametersState = parametersState
    )
}


@Composable
fun ExerciseWidget(
    exercise: Exercise,
    onClick: (Exercise) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(
                onClick = {
                    onClick(exercise)
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
