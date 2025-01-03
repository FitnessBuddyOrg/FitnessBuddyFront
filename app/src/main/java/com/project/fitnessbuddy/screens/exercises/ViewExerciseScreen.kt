package com.project.fitnessbuddy.screens.exercises

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.navigation.DefaultTitleWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun ViewExerciseScreen(
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
            navigationViewModel.onEvent(NavigationEvent.DisableAllButtons)
            navigationViewModel.onEvent(NavigationEvent.EnableBackButton)

            navigationViewModel.onEvent(NavigationEvent.EnableEditButton)
            navigationViewModel.onEvent(NavigationEvent.SetEditButtonRoute(context.getString(R.string.add_edit_exercise)))

            navigationViewModel.onEvent(NavigationEvent.EnableDeleteButton)
            navigationViewModel.onEvent(NavigationEvent.SetOnDeleteButtonClicked {
                onDeleteExercise(
                    navigationState = navigationState,
                    exercisesViewModel = exercisesViewModel,
                    exercisesState = exercisesState,
                    context = context
                )
            })

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                exercisesState.editingExercise.name.let { DefaultTitleWidget(it) }
            })
        }

        onDispose {
            job.cancel()
        }
    }

    TabLayout()
}

fun onDeleteExercise(
    navigationState: NavigationState,
    exercisesViewModel: ExercisesViewModel,
    exercisesState: ExercisesState,
    context: Context
) {
    if(exercisesViewModel.onEvent(ExercisesEvent.DeleteExercise(exercisesState.editingExercise))) {
        navigationState.navController?.navigateUp()
        Toast.makeText(context, "Deleted ${exercisesState.editingExercise.name}", Toast.LENGTH_SHORT).show()
        exercisesViewModel.onEvent(ExercisesEvent.ResetEditingExercise)
    } else {
        Toast.makeText(context, "Failed to delete ${exercisesState.editingExercise.name}", Toast.LENGTH_SHORT).show()
    }

}

@Composable
fun TabLayout() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("ABOUT", "HISTORY", "CHARTS")

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> AboutTab()
            1 -> HistoryTab()
            2 -> ChartsTab()
        }
    }
}

@Composable
fun AboutTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for Tab 1")
    }
}

@Composable
fun HistoryTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for Tab 2")
    }
}

@Composable
fun ChartsTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for Tab 3")
    }
}
