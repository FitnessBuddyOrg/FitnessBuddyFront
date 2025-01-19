package com.project.fitnessbuddy.screens.routines

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.SleekButton
import com.project.fitnessbuddy.screens.common.SleekErrorButton
import com.project.fitnessbuddy.screens.common.formatElapsedSeconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun StartRoutineScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,
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
                    navigationState.navController?.navigate(context.getString(R.string.view_routine_route))
                }
            ))

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                routinesState.selectedRoutineDTO.name.let { MediumTextWidget("${stringResource(R.string.started_routine)} $it") }
            })
        }

        onDispose {
            job.cancel()
        }
    }


    MainTab(
        navigationState = navigationState,
        navigationViewModel = navigationViewModel,
        routinesState = routinesState,
        routinesViewModel = routinesViewModel,
        context = context
    )
}


@Composable
fun MainTab(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,
    routinesState: RoutinesState,
    routinesViewModel: RoutinesViewModel,
    context: Context
) {
    val allChecked = remember { mutableStateOf(false) }
    ObserveAppLifecycle(routinesViewModel)

    var elapsedSeconds by remember { mutableLongStateOf(0) }
    val startDate by remember { mutableStateOf(routinesState.selectedRoutineDTO.routine.startDate) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedSeconds = startDate?.let {
                ((Date().time - it.time) / 1000)
            } ?: 0
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TimerText(
                modifier = Modifier
                    .fillMaxWidth(),
                elapsedSeconds = elapsedSeconds
            )
            Spacer(modifier = Modifier.height(16.dp))

            AllExercisesWidget(
                routinesState = routinesState,
                routinesViewModel = routinesViewModel,
                navigationViewModel = navigationViewModel,
                navigationState = navigationState,

                padding = padding,
                context = context,

                editingEnabled = true,
                checkboxEnabled = true,

                onAllCheckedChange = { isAllChecked ->
                    allChecked.value = isAllChecked
                },

                footerItem = {
                    SleekErrorButton(
                        text = stringResource(R.string.cancel_routine),
                        onClick = {
                            navigationState.navController?.navigate(context.getString(R.string.view_routine_route))
                        }
                    )
                    SleekButton(
                        text = stringResource(R.string.finish),
                        onClick = {
                            if (allChecked.value) {
                                routinesViewModel.onEvent(RoutinesEvent.CompleteRoutine)

                                navigationState.navController?.navigate(context.getString(R.string.completed_routine_route))
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.complete_all_sets_toast,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            )
        }

    }
}

@Composable
fun TimerText(
    modifier: Modifier = Modifier,
    elapsedSeconds: Long
) {
    val formattedTime = elapsedSeconds.formatElapsedSeconds()

    Text(
        modifier = modifier,
        text = formattedTime,
        style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    )
}

@Composable
fun ObserveAppLifecycle(
    routinesViewModel: RoutinesViewModel
) {
    val context = LocalContext.current

    val lifecycleOwner = rememberUpdatedState(ProcessLifecycleOwner.get())

    DisposableEffect(lifecycleOwner.value) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    routinesViewModel.sendRoutineUpdate(context)
                }

                Lifecycle.Event.ON_START -> {
                    context.stopService(
                        Intent(
                            context,
                            StartRoutineServiceNotification::class.java
                        )
                    )
                }

                else -> {}
            }
        }

        lifecycleOwner.value.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.value.lifecycle.removeObserver(observer)
        }
    }
}
