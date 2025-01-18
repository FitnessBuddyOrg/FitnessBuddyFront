package com.project.fitnessbuddy.screens.routines

import android.content.Context
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.SleekButton
import com.project.fitnessbuddy.screens.common.SleekErrorButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

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
            navigationViewModel.onEvent(NavigationEvent.SetBackButton(navigationState.navController))

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                routinesState.selectedRoutineDTO.name.let { MediumTextWidget("${stringResource(R.string.started_routine)} $it") }
            })

            navigationViewModel.onEvent(NavigationEvent.AddTopBarActions {

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
    val startDate = Date()

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
            TimerScreen(
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = {
//                    routinesViewModel.onEvent(RoutinesEvent.SetElapsedSeconds(it))
                }
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
                            navigationState.navController?.navigateUp()
                        }
                    )
                    SleekButton(
                        text = stringResource(R.string.finish),
                        onClick = {
                            if (allChecked.value) {
                                routinesViewModel.onEvent(RoutinesEvent.CompleteRoutine(startDate))
                                navigationState.navController?.navigateUp()
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
fun TimerScreen(
    onValueChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedSeconds++
            onValueChange(elapsedSeconds)
        }
    }

    val formattedTime =
        String.format(Locale.getDefault(), "%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)

    Text(
        modifier = modifier,
        text = formattedTime,
        style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    )
}
