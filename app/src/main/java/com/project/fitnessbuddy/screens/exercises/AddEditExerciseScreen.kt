package com.project.fitnessbuddy.screens.exercises

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.entity.enums.Category
import com.project.fitnessbuddy.database.entity.enums.ShareType
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.DefaultTextArea
import com.project.fitnessbuddy.screens.common.DefaultTextField
import com.project.fitnessbuddy.screens.common.DialogRadioButtonList
import com.project.fitnessbuddy.screens.common.StoredValue
import com.project.fitnessbuddy.screens.common.ValidationFloatingActionButton
import kotlinx.coroutines.launch

@Composable
fun AddEditExerciseScreen(
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
                MediumTextWidget(
                    "${stringResource(exercisesState.editType.resourceId)} ${
                        stringResource(
                            R.string.an_exercise
                        )
                    }"
                )
            })
        }

        onDispose {
            job.cancel()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
        floatingActionButton = {
            fun getSuccessMessage(): String {
                return if (exercisesState.editType == EditType.ADD) {
                    "${context.getString(R.string.saved_exercise)} ${exercisesState.selectedExercise.name}"
                } else {
                    "${context.getString(R.string.updated_exercise)} ${exercisesState.selectedExercise.name}"
                }
            }

            fun getFailureMessage(): String {
                return if (exercisesState.editType == EditType.ADD) {
                    context.getString(R.string.savednt_exercise)
                } else {
                    context.getString(R.string.updatednt_exercise)
                }
            }

            ValidationFloatingActionButton(
                context = context,
                onClick = ({exercisesViewModel.onEvent(ExercisesEvent.UpsertExercise)}),
                onSuccess = {
                    navigationState.navController?.navigateUp()
                },
                successMessage = getSuccessMessage(),
                failureMessage = getFailureMessage()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            DefaultTextField(
                label = stringResource(R.string.name),
                value = exercisesState.selectedExercise.name,
                onValueChange = {
                    exercisesViewModel.onEvent(ExercisesEvent.SetName(it))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            DefaultTextArea(
                label = stringResource(R.string.instructions),
                value = exercisesState.selectedExercise.instructions,
                onValueChange = {
                    exercisesViewModel.onEvent(ExercisesEvent.SetInstructions(it))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            DefaultTextField(
                label = stringResource(R.string.video_link),
                value = exercisesState.selectedExercise.videoLink,
                onValueChange = {
                    exercisesViewModel.onEvent(ExercisesEvent.SetVideoLink(it))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            DialogRadioButtonList(
                label = stringResource(R.string.category),
                options = Category.entries.map { StoredValue(it, stringResource(it.resourceId)) },
                initialStoredValue = StoredValue(
                    exercisesState.selectedExercise.category,
                    stringResource(exercisesState.selectedExercise.category.resourceId)
                ),
                onValueChange = {
                    exercisesViewModel.onEvent(ExercisesEvent.SetCategory(it.value))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            DialogRadioButtonList(
                label = stringResource(R.string.share_type),
                options = ShareType.entries.map { StoredValue(it, stringResource(it.resourceId)) },
                initialStoredValue = StoredValue(
                    exercisesState.selectedExercise.shareType,
                    stringResource(exercisesState.selectedExercise.shareType.resourceId)
                ),
                onValueChange = {
                    exercisesViewModel.onEvent(ExercisesEvent.SetShareType(it.value))
                }
            )
        }
    }
}


