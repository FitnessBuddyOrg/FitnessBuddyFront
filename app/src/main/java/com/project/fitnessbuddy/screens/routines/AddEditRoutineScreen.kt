package com.project.fitnessbuddy.screens.routines

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
import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.database.entity.enums.Category
import com.project.fitnessbuddy.database.entity.enums.Frequency
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
import com.project.fitnessbuddy.screens.exercises.ExercisesEvent
import kotlinx.coroutines.launch

@Composable
fun AddEditRoutineScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,
    routinesState: RoutinesState,
    routinesViewModel: RoutinesViewModel
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
                MediumTextWidget(
                    "${stringResource(routinesState.editType.resourceId)} ${
                        stringResource(
                            R.string.a_routine
                        )
                    }"
                )
            })

            if (routinesState.editType == EditType.ADD) {
                routinesViewModel.onEvent(
                    RoutinesEvent.SetSelectedRoutineDTO(RoutineDTO())
                )
            }
        }

        onDispose {
            job.cancel()
        }
    }

    InputInformation(
        navigationState = navigationState,
        navigationViewModel = navigationViewModel,
        routinesState = routinesState,
        routinesViewModel = routinesViewModel
    )
}


@Composable
fun InputInformation(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,
    routinesState: RoutinesState,
    routinesViewModel: RoutinesViewModel
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
                    val succeeded: Boolean = if (routinesState.editType == EditType.ADD) {
                        routinesViewModel.onEvent(RoutinesEvent.SaveRoutine)
                    } else {
                        routinesViewModel.onEvent(RoutinesEvent.UpdateRoutine)
                    }

                    if (succeeded) {
                        navigationState.navController?.navigateUp()
                        Toast.makeText(
                            context,
                            "${context.getString(R.string.saved_routine)} ${routinesState.selectedRoutineDTO.routine.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, context.getString(R.string.savednt_routine), Toast.LENGTH_SHORT)
                            .show()
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
                value = routinesState.selectedRoutineDTO.routine.name,
                onValueChange = {
                    routinesViewModel.onEvent(RoutinesEvent.SetName(it))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            DialogRadioButtonList(
                label = stringResource(R.string.frequency),
                options = Frequency.entries.map { StoredValue(it, stringResource(it.resourceId)) },
                initialStoredValue = StoredValue(
                    routinesState.selectedRoutineDTO.routine.frequency,
                    stringResource(routinesState.selectedRoutineDTO.routine.frequency.resourceId)
                ),
                onValueChange = {
                    routinesViewModel.onEvent(RoutinesEvent.SetFrequency(it.value))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            DialogRadioButtonList(
                label = stringResource(R.string.share_type),
                options = ShareType.entries.map { StoredValue(it, stringResource(it.resourceId)) },
                initialStoredValue = StoredValue(
                    routinesState.selectedRoutineDTO.routine.shareType,
                    stringResource(routinesState.selectedRoutineDTO.routine.shareType.resourceId)
                ),
                onValueChange = {
                    routinesViewModel.onEvent(RoutinesEvent.SetShareType(it.value))
                }
            )
        }


    }
}
