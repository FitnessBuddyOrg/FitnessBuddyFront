package com.project.fitnessbuddy.screens.exercises

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.entity.Category
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.ShareType
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.DefaultTextArea
import com.project.fitnessbuddy.screens.common.DefaultTextField
import com.project.fitnessbuddy.screens.common.DialogRadioButtonList
import com.project.fitnessbuddy.screens.common.Language
import com.project.fitnessbuddy.screens.common.StoredValue
import kotlinx.coroutines.launch

@Composable
fun AddEditExerciseScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,
    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = remember {
        lifecycleOwner.lifecycleScope
    }

    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            navigationViewModel.onEvent(NavigationEvent.DisableAllButtons)
            navigationViewModel.onEvent(NavigationEvent.EnableBackButton)

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                MediumTextWidget("${stringResource(exercisesState.editType.resourceId)} ${stringResource(R.string.exercise)}")
            })

            if (exercisesState.editType == EditType.ADD) {
                exercisesViewModel.onEvent(ExercisesEvent.SetEditingExercise(
                    Exercise(
                        name = "",
                        instructions = "",
                        videoLink = "",
                        category = Category.CHEST,
                        shareType = ShareType.PUBLIC,
                        language = Language.ENGLISH
                    )
                ))
            }
        }

        onDispose {
            job.cancel()
        }
    }

    InputInformation(
        navigationState = navigationState,
        navigationViewModel = navigationViewModel,
        exercisesState = exercisesState,
        exercisesViewModel = exercisesViewModel,
    )
}

@Composable
fun InputInformation(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,
    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel
) {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
        floatingActionButton = {
            IconButton(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                onClick = {
                    val succeeded: Boolean = if(exercisesState.editType == EditType.ADD) {
                        exercisesViewModel.onEvent(ExercisesEvent.SaveExercise)
                    } else {
                        exercisesViewModel.onEvent(ExercisesEvent.UpdateExercise)
                    }

                    if (succeeded) {
                        navigationState.navController?.navigateUp()
                        Toast.makeText(context, "Saved Exercise ${exercisesState.selectedExercise.name}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Could not save exercise", Toast.LENGTH_SHORT).show()
                    }
                },
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
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
                storedValue = StoredValue(exercisesState.selectedExercise.category, stringResource(exercisesState.selectedExercise.category.resourceId)),
                onValueChange = {
                    exercisesViewModel.onEvent(ExercisesEvent.SetCategory(it.value))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            DialogRadioButtonList(
                label = stringResource(R.string.share_type),
                options = ShareType.entries.map { StoredValue(it, stringResource(it.resourceId))},
                storedValue = StoredValue(exercisesState.selectedExercise.shareType, stringResource(exercisesState.selectedExercise.shareType.resourceId)),
                onValueChange = {
                    exercisesViewModel.onEvent(ExercisesEvent.SetShareType(it.value))
                }
            )
        }


    }


}
