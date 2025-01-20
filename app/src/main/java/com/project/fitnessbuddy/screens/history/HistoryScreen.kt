package com.project.fitnessbuddy.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.GroupedWidgetList
import com.project.fitnessbuddy.screens.common.ParametersState
import com.project.fitnessbuddy.screens.common.ParametersViewModel
import com.project.fitnessbuddy.screens.common.format
import com.project.fitnessbuddy.screens.routines.RoutineSummaryCard
import com.project.fitnessbuddy.screens.routines.RoutinesState
import com.project.fitnessbuddy.screens.routines.RoutinesViewModel
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,

    routinesState: RoutinesState,
    routinesViewModel: RoutinesViewModel,

    parametersState: ParametersState,
    parametersViewModel: ParametersViewModel
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
                MediumTextWidget(context.getString(R.string.history))
            })
        }

        onDispose {
            job.cancel()
        }
    }

    GroupedWidgetList(
        itemsList = routinesState.completedRoutineDTOs,
        widget = @Composable {
            RoutineSummaryCard(
                routineDTO = it,
                context = context,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            )
        },
        verticalArrangement = Arrangement.spacedBy(16.dp),

        keySelector = {
            it.routine.startDate?.format("MMMM").toString().uppercase()
        },
        predicate = {
            true
        },
    )
}
