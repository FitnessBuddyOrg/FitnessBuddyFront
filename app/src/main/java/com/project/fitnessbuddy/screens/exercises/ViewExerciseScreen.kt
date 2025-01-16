package com.project.fitnessbuddy.screens.exercises

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.navigation.CreateButton
import com.project.fitnessbuddy.navigation.DeleteButton
import com.project.fitnessbuddy.navigation.EditButton
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.navigation.LargeTextWidget
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.navigation.SearchButton
import com.project.fitnessbuddy.screens.common.StoredValue
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
            navigationViewModel.onEvent(NavigationEvent.ClearTopBarActions)
            navigationViewModel.onEvent(NavigationEvent.DisableAllButtons)
            navigationViewModel.onEvent(NavigationEvent.EnableBackButton)

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                exercisesState.selectedExercise.name.let { MediumTextWidget(it) }
            })

            navigationViewModel.onEvent(NavigationEvent.AddTopBarActions {
                EditButton(
                    onClick = {
                        exercisesViewModel.onEvent(ExercisesEvent.SetEditType(EditType.EDIT))
                        navigationState.navController?.navigate(context.getString(R.string.add_edit_exercise))
                    }
                )
                DeleteButton(
                    onClick = {
                        onDeleteExercise(
                            navigationState = navigationState,
                            exercisesViewModel = exercisesViewModel,
                            exercisesState = exercisesState,
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

    TabLayout(
        exercisesState = exercisesState,
        exercisesViewModel = exercisesViewModel
    )
}

fun onDeleteExercise(
    navigationState: NavigationState,
    exercisesViewModel: ExercisesViewModel,
    exercisesState: ExercisesState,
    context: Context
) {
    if (exercisesViewModel.onEvent(ExercisesEvent.DeleteExercise(exercisesState.selectedExercise))) {
        navigationState.navController?.navigateUp()
        Toast.makeText(
            context,
            "Deleted ${exercisesState.selectedExercise.name}",
            Toast.LENGTH_SHORT
        ).show()
        exercisesViewModel.onEvent(ExercisesEvent.ResetSelectedExercise)
    } else {
        Toast.makeText(
            context,
            "Failed to delete ${exercisesState.selectedExercise.name}",
            Toast.LENGTH_SHORT
        ).show()
    }

}

@Composable
fun TabLayout(
    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            ViewExerciseTab.entries.map { StoredValue(it, stringResource(it.resourceId)) }
                .forEachIndexed { index, storedValue ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = storedValue.displayValue.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    )
                }
        }
        when (selectedTabIndex) {
            0 -> AboutTab(
                exercisesState = exercisesState,
                exercisesViewModel = exercisesViewModel
            )

            1 -> HistoryTab()
            2 -> ChartsTab()
        }
    }
}

@Composable
fun AboutTab(
    exercisesState: ExercisesState,
    exercisesViewModel: ExercisesViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (exercisesState.selectedExercise.videoLink.isNotEmpty() && isValidYtLink(exercisesState.selectedExercise.videoLink)) {
            val videoId = extractIdFromYtLink(exercisesState.selectedExercise.videoLink)
            LiveTvScreen(videoId)
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = stringResource(R.string.no_vid_available)
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LargeTextWidget(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.instructions)
            )
            MediumTextWidget(
                modifier = Modifier.fillMaxWidth(),
                text = exercisesState.selectedExercise.instructions
            )
        }

    }
}

fun extractIdFromYtLink(link: String): String {
    val regex =
        "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embed&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*"
    val pattern = Regex(regex)
    val matchResult = pattern.find(link)
    return matchResult?.value ?: ""
}

fun isValidYtLink(link: String): Boolean {
    val regex = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+"
    return link.matches(Regex(regex))
}

@Composable
fun LiveTvScreen(
    videoId: String
) {
    val ctx = LocalContext.current
    AndroidView(factory = {
        var view = YouTubePlayerView(it)
        val fragment = view.addYouTubePlayerListener(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(
                    youTubePlayer:
                    YouTubePlayer
                ) {
                    super.onReady(youTubePlayer)
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            }
        )
        view
    })
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
