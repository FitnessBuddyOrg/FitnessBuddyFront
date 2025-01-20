package com.project.fitnessbuddy.screens.routines

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.MainActivity
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.navigation.DeleteButton
import com.project.fitnessbuddy.navigation.EditButton
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.BetterButton
import com.project.fitnessbuddy.screens.common.SelectedExerciseWidget
import com.project.fitnessbuddy.screens.common.timeAgo
import com.project.fitnessbuddy.screens.exercises.ExercisesEvent
import com.project.fitnessbuddy.screens.exercises.ExercisesState
import com.project.fitnessbuddy.screens.exercises.ExercisesViewModel
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun ViewRoutineScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,

    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel,

    routinesState: RoutinesState,
    routinesViewModel: RoutinesViewModel
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
                    navigationState.navController?.navigate(context.getString(R.string.routines_route))
                }
            ))

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                routinesState.selectedRoutineDTO.name.let { MediumTextWidget(it) }
            })

            navigationViewModel.onEvent(NavigationEvent.AddTopBarActions {
                EditButton(
                    onClick = {
                        routinesViewModel.onEvent(RoutinesEvent.SetEditType(EditType.EDIT))
                        navigationState.navController?.navigate(context.getString(R.string.add_edit_routine_route))
                    }
                )
                DeleteButton(
                    onClick = {
                        onDeleteRoutine(
                            navigationState = navigationState,
                            routinesViewModel = routinesViewModel,
                            routinesState = routinesState,
                            context = context
                        )
                    }
                )
            })
        }

        onDispose {
            job.cancel()
        }
    }

    val activity = LocalContext.current as MainActivity

    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true)
        }

    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if(!isGranted) {
                shouldShowRequestPermissionRationale(activity, "Notifications are used to: \n- Notify you when a routine is running in the background")
            }
        }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = padding,
            ) {
                if (routinesState.selectedRoutineDTO.routine.lastPerformed != null) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.titleLarge,
                            text = "${stringResource(R.string.last_performed)}: ${routinesState.selectedRoutineDTO.routine.lastPerformed?.timeAgo(context)}",

                            )
                    }
                }

                items(routinesState.selectedRoutineDTO.routineExerciseDTOs) { routineExerciseDTO ->
                    SelectedExerciseWidget(
                        exercise = routineExerciseDTO.exercise,
                        onClick = { exercise, _ ->
                            exercisesViewModel.onEvent(ExercisesEvent.SetSelectedExercise(exercise))
                            navigationState.navController?.navigate(context.getString(R.string.view_exercise_route))
                        },
                        titleText = "${routineExerciseDTO.routineExerciseSetDTOs.size} × ${routineExerciseDTO.exercise.name}"
                    )

                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            BetterButton(
                text = stringResource(R.string.start_routine),
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    routinesViewModel.onEvent(RoutinesEvent.SetStartDate(Date()))
                    navigationState.navController?.navigate(context.getString(R.string.start_routine_route))
                }
            )
        }
    }
}

fun onDeleteRoutine(
    navigationState: NavigationState,
    routinesViewModel: RoutinesViewModel,
    routinesState: RoutinesState,
    context: Context
) {
    if (routinesViewModel.onEvent(RoutinesEvent.DeleteRoutine(routinesState.selectedRoutineDTO.routine))) {
        navigationState.navController?.navigateUp()
        Toast.makeText(
            context,
            "${context.getString(R.string.deleted)} ${routinesState.selectedRoutineDTO.name}",
            Toast.LENGTH_SHORT
        ).show()
    } else {
        Toast.makeText(
            context,
            "${context.getString(R.string.failed_to_delete)} ${routinesState.selectedRoutineDTO.name}",
            Toast.LENGTH_SHORT
        ).show()
    }
}
