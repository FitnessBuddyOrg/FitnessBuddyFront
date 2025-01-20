package com.project.fitnessbuddy.screens.exercises

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.api.auth.UserState
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.navigation.CreateButton
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.navigation.SearchButton
import com.project.fitnessbuddy.screens.common.GroupedWidgetList
import com.project.fitnessbuddy.screens.common.ParametersState
import com.project.fitnessbuddy.screens.common.ParametersViewModel
import com.project.fitnessbuddy.screens.common.SelectedExerciseWidget
import com.project.fitnessbuddy.screens.common.StoredValue
import kotlinx.coroutines.launch

@Composable
fun ExercisesScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,

    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel,

    parametersState: ParametersState,
    parametersViewModel: ParametersViewModel,

    userState: UserState,
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
                        exercisesViewModel.onEvent(ExercisesEvent.SetSelectedExercise(Exercise(userId = userState.user.userId)))
                        navigationState.navController?.navigate(context.getString(R.string.add_edit_exercise_route))
                    }
                )
            })
        }

        onDispose {
            job.cancel()
        }
    }

    ExercisesGroupedList(
        parametersState = parametersState,
        exercisesViewModel = exercisesViewModel,
        exercisesState = exercisesState,
        navigationState = navigationState,
        context = context
    )
}

@Composable
fun ExercisesGroupedList(
    parametersState: ParametersState,
    exercisesViewModel: ExercisesViewModel,
    exercisesState: ExercisesState,
    navigationState: NavigationState,
    context: Context,

    floatingActionButton: @Composable () -> Unit = {},
    initialSelected: (Exercise) -> Boolean = { false },
    selectionEnabled: Boolean = false,

    onWidgetClick: (Exercise, Boolean) -> Unit = { exercise, _ ->
        exercisesViewModel.onEvent(ExercisesEvent.SetSelectedExercise(exercise))
        navigationState.navController?.navigate(context.getString(R.string.view_exercise_route))
    }
) {
    GroupedWidgetList(
        itemsList = exercisesState.exercises,
        widget = @Composable {
            SelectedExerciseWidget(
                exercise = it,
                onClick = onWidgetClick,
                initialSelected = initialSelected(it),
                selectionEnabled = selectionEnabled
            )
        },
        header = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SortType.entries.map { StoredValue(it, stringResource(it.resourceId)) }
                    .forEach { storedValue ->
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
                                    exercisesViewModel.onEvent(
                                        ExercisesEvent.SortExercises(
                                            storedValue.value
                                        )
                                    )
                                },
                            )
                            Text(
                                text = storedValue.displayValue.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
            }
        },
        keySelector = {
            when (exercisesState.sortType) {
                SortType.NAME -> it.name.first().uppercase()
                SortType.CATEGORY -> context.getString(it.category.resourceId)
            }
        },
        predicate = {
            (it.language.name == parametersState.languageParameter.value) || it.language.isCustom
        },
        floatingActionButton = floatingActionButton
    )
}
