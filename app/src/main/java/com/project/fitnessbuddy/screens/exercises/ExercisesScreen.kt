package com.project.fitnessbuddy.screens.exercises

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.StoredValue
import com.project.fitnessbuddy.screens.common.WidgetLetterImage
import kotlinx.coroutines.launch

@Composable
fun ExercisesScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,
    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel
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
            navigationViewModel.onEvent(NavigationEvent.SetAddButtonRoute(context.getString(R.string.add_edit_exercise_route)))

            navigationViewModel.onEvent(NavigationEvent.SetTitle(context.getString(R.string.exercises)))
            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                MediumTextWidget(context.getString(R.string.exercises))
            })
        }

        onDispose {
            job.cancel()
        }
    }

    AlphabeticallyGroupedWidgetList(
        exercisesState = exercisesState,
        exercisesViewModel = exercisesViewModel,
        navigationState = navigationState
    )
}

@Composable
fun AlphabeticallyGroupedWidgetList(
    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel,
    navigationState: NavigationState
) {
    val context = LocalContext.current


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SortType.entries.map { StoredValue(it, stringResource(it.resourceId)) }.forEach { storedValue ->
                        Row(
                            modifier = Modifier
                                .weight(0.5f)
                                .pointerInput(Unit) {
                                    detectTapGestures(onPress = {
                                        exercisesViewModel.onEvent(
                                            ExercisesEvent.SortExercises(
                                                storedValue.value
                                            )
                                        )
                                    })
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            RadioButton(
                                selected = exercisesState.sortType == storedValue.value,
                                onClick = {
                                    exercisesViewModel.onEvent(ExercisesEvent.SortExercises(storedValue.value))
                                },
                            )
                            Text(
                                text = storedValue.displayValue.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }

            exercisesState.exercises
                .groupBy {
                    when (exercisesState.sortType) {
                        SortType.NAME -> it.name.first().uppercase()
                        SortType.CATEGORY -> context.getString(it.category.resourceId)
                    }
                }
                .toSortedMap()
                .forEach { (letter, exercisesInGroup) ->
                    item {
                        Text(
                            text = letter.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
                        )
                    }
                    items(exercisesInGroup) { exercise ->
                        ExerciseWidget(
                            exercise = exercise,
                            navigationState = navigationState,
                            exercisesViewModel = exercisesViewModel
                        )
                    }
                }
        }
    }

}

@Composable
fun ExerciseWidget(
    exercise: Exercise,
    navigationState: NavigationState,
    exercisesViewModel: ExercisesViewModel
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(
                onClick = {
                    exercisesViewModel.onEvent(ExercisesEvent.SetEditingExercise(exercise))
                    navigationState.navController?.navigate(context.getString(R.string.view_exercise_route))
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
