package com.project.fitnessbuddy.screens.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.entity.enums.CustomState
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.DefaultTextField
import com.project.fitnessbuddy.screens.common.SelectedExerciseWidget
import com.project.fitnessbuddy.screens.common.ValidationFloatingActionButton
import kotlinx.coroutines.launch

@Composable
fun AddExerciseCodeScreen(
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
            navigationViewModel.onEvent(NavigationEvent.DisableCustomButton)
            navigationViewModel.onEvent(NavigationEvent.ClearTopBarActions)

            navigationViewModel.onEvent(NavigationEvent.EnableCustomButton)
            navigationViewModel.onEvent(NavigationEvent.SetBackButton(navigationState.navController))

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                MediumTextWidget(stringResource(R.string.add_exercise_from_code))
            })
        }

        onDispose {
            job.cancel()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        floatingActionButton = {
            fun onEvent(): Boolean {
                return if (exercisesState.selectedExercise.name.isNotEmpty() && exercisesState.exerciseFetched == CustomState.TRUE) {
                    exercisesViewModel.onEvent(ExercisesEvent.UpsertExercise)
                } else {
                    false
                }
            }

            ValidationFloatingActionButton(
                context = context,
                onClick = ({ onEvent() }),
                onSuccess = {
                    navigationState.navController?.navigateUp()
                },
                successMessage = "${context.getString(R.string.saved_exercise)} ${exercisesState.selectedExercise.name}",
                failureMessage = context.getString(R.string.savednt_exercise)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DefaultTextField(
                    modifier = Modifier.weight(0.9f),
                    label = stringResource(R.string.exercise_code),
                    value = exercisesState.fetchedExerciseToken,
                    onValueChange = {
                        exercisesViewModel.onEvent(ExercisesEvent.SetFetchedExerciseToken(it))
                    }
                )
                IconButton(
                    modifier = Modifier.weight(0.1f),
                    onClick = {

                        exercisesViewModel.onEvent(ExercisesEvent.FetchExerciseByToken)

                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Arrow Forward"
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (exercisesState.selectedExercise.name.isNotEmpty() && exercisesState.exerciseFetched == CustomState.TRUE) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = stringResource(R.string.fetched_exercise),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                SelectedExerciseWidget(
                    exercise = exercisesState.selectedExercise,
                    onClick = { _, _ ->
                        navigationState.navController?.navigate(context.getString(R.string.view_exercise_route))
                    },
                    initialSelected = false,
                    selectionEnabled = false
                )
            } else if (exercisesState.exerciseFetched == CustomState.FALSE) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = stringResource(R.string.no_exercise_fetched),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }
        }
    }
}
