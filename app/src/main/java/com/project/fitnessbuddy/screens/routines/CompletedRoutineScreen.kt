package com.project.fitnessbuddy.screens.routines

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.format
import com.project.fitnessbuddy.screens.common.timeInLetters
import kotlinx.coroutines.launch

@Composable
fun CompletedRoutineScreen(
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
            navigationViewModel.onEvent(
                NavigationEvent.SetCloseButton(
                    onClick = {
                        navigationState.navController?.navigate(context.getString(R.string.routines_route))
                    }
                ))

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                routinesState.selectedRoutineDTO.name.let { MediumTextWidget("${stringResource(R.string.completed_routine)} $it") }
            })
        }

        onDispose {
            job.cancel()
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {

        }
    }

    WorkoutSummaryScreen(
        routinesViewModel = routinesViewModel,
        routinesState = routinesState,
        context = context
    )
}

@Composable
fun WorkoutSummaryScreen(
    routinesViewModel: RoutinesViewModel,
    routinesState: RoutinesState,
    context: Context
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "${stringResource(R.string.congratulations)}!",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "${stringResource(R.string.routine_number)} ${routinesState.completedRoutineDTOs.size}",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            RoutineSummaryCard(
                routineDTO = routinesState.selectedRoutineDTO,
                context = context
            )
        }
    }
}

@Composable
fun WorkoutStat(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun RoutineSummaryCard(
    routineDTO: RoutineDTO,
    context: Context,
    modifier: Modifier = Modifier
) {
    val finishDate =
        routineDTO.routine.startDate?.format("MMMM dd").toString()
    val duration =
        routineDTO.routine.startDate?.timeInLetters(context).toString()
    val totalWeight =
        routineDTO.routineExerciseDTOs.sumOf { it -> it.routineExerciseSetDTOs.sumOf { it.weight * it.reps } }
            .toString()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(8.dp))
            .clickable(
                onClick = {

                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = routineDTO.name,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = finishDate,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                WorkoutStat(Icons.Default.Timer, duration)
                WorkoutStat(
                    Icons.Default.FitnessCenter,
                    "$totalWeight ${context.getString(R.string.kg)}"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.exercises),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = stringResource(R.string.best_set),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                routineDTO.routineExerciseDTOs.forEach { routineExerciseDTO ->
                    val bestRoutineExerciseSetDTOWeight =
                        routineExerciseDTO.routineExerciseSetDTOs.maxOf { (it.weight * it.reps) }
                    val bestRoutineExerciseSetDTO =
                        routineExerciseDTO.routineExerciseSetDTOs.find { (it.weight * it.reps) == bestRoutineExerciseSetDTOWeight }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = routineExerciseDTO.exercise.name,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                        )
                        Text(
                            text = "${bestRoutineExerciseSetDTO?.weight} ${
                                context.getString(
                                    R.string.kg
                                )
                            } × ${bestRoutineExerciseSetDTO?.reps}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }

            }
        }
    }
}


