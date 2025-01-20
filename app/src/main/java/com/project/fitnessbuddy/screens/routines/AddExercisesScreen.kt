package com.project.fitnessbuddy.screens.routines

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
import com.project.fitnessbuddy.screens.common.ValidationFloatingActionButton
import com.project.fitnessbuddy.screens.exercises.ExercisesEvent
import com.project.fitnessbuddy.screens.exercises.ExercisesGroupedList
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
    parametersViewModel: ParametersViewModel,

    userState: UserState
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
                    navigationState.navController?.navigateUp()
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
    fun isSelected(exercise: Exercise): Boolean {
        val routineExerciseDTO =
            routinesState.selectedRoutineDTO.routineExerciseDTOs.find {
                it.routineExercise.exerciseId == exercise.exerciseId &&
                        it.routineExercise.routineId == routinesState.selectedRoutineDTO.routine.routineId
            }
        return routineExerciseDTO != null
    }

    ExercisesGroupedList(
        parametersState = parametersState,
        exercisesViewModel = exercisesViewModel,
        exercisesState = exercisesState,
        navigationState = navigationState,
        context = context,
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
        initialSelected = ::isSelected,
        selectionEnabled = true,
        onWidgetClick = { exercise, selected ->
            routinesViewModel.onEvent(RoutinesEvent.HandleExercise(exercise, selected))
        }
    )
}


