package com.project.fitnessbuddy.screens.routines

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.entity.enums.Frequency
import com.project.fitnessbuddy.database.entity.enums.ShareType
import com.project.fitnessbuddy.navigation.DeleteButton
import com.project.fitnessbuddy.navigation.EditType
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.navigation.SmallTextWidget
import com.project.fitnessbuddy.screens.common.CustomIntegerField
import com.project.fitnessbuddy.screens.common.DefaultTextField
import com.project.fitnessbuddy.screens.common.DialogRadioButtonList
import com.project.fitnessbuddy.screens.common.SleekButton
import com.project.fitnessbuddy.screens.common.StoredValue
import com.project.fitnessbuddy.screens.common.ValidationFloatingActionButton
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
            navigationViewModel.onEvent(NavigationEvent.DisableCustomButton)
            navigationViewModel.onEvent(NavigationEvent.ClearTopBarActions)

            navigationViewModel.onEvent(NavigationEvent.EnableCustomButton)
            navigationViewModel.onEvent(NavigationEvent.SetBackButton(navigationState.navController))

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                MediumTextWidget(
                    "${stringResource(routinesState.editType.resourceId)} ${
                        stringResource(
                            R.string.a_routine
                        )
                    }"
                )
            })
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
            fun getSuccessMessage(): String {
                return if (routinesState.editType == EditType.ADD) {
                    "${context.getString(R.string.saved_routine)} ${routinesState.selectedRoutineDTO.name}"
                } else {
                    "${context.getString(R.string.updated_routine)} ${routinesState.selectedRoutineDTO.name}"
                }
            }

            fun getFailureMessage(): String {
                return if (routinesState.editType == EditType.ADD) {
                    context.getString(R.string.savednt_routine)
                } else {
                    context.getString(R.string.updatednt_routine)
                }
            }

            ValidationFloatingActionButton(
                context = context,
                onClick = ({ routinesViewModel.onEvent(RoutinesEvent.UpsertSelectedRoutineDTO) }),
                onSuccess = {
                    navigationState.navController?.navigateUp()
                },
                successMessage = getSuccessMessage(),
                failureMessage = getFailureMessage()

            )
        }
    ) { padding ->

        AllExercisesWidget(
            routinesState = routinesState,
            routinesViewModel = routinesViewModel,
            navigationViewModel = navigationViewModel,
            navigationState = navigationState,

            padding = padding,
            context = context,

            editingEnabled = true,
            checkboxEnabled = false,

            headerItem = {
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
                    options = Frequency.entries.map {
                        StoredValue(
                            it,
                            stringResource(it.resourceId)
                        )
                    },
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
                    options = ShareType.entries.map {
                        StoredValue(
                            it,
                            stringResource(it.resourceId)
                        )
                    },
                    initialStoredValue = StoredValue(
                        routinesState.selectedRoutineDTO.routine.shareType,
                        stringResource(routinesState.selectedRoutineDTO.routine.shareType.resourceId)
                    ),
                    onValueChange = {
                        routinesViewModel.onEvent(RoutinesEvent.SetShareType(it.value))
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        )
    }
}

@Composable
fun AllExercisesWidget(
    routinesState: RoutinesState,
    routinesViewModel: RoutinesViewModel,

    navigationViewModel: NavigationViewModel,
    navigationState: NavigationState,

    context: Context,
    padding: PaddingValues,

    headerItem: @Composable () -> Unit = {},
    footerItem: @Composable () -> Unit = {},

    editingEnabled: Boolean,
    checkboxEnabled: Boolean,

    onAllCheckedChange: (Boolean) -> Unit = {}
) {

    val allChecked =
        routinesState.selectedRoutineDTO.routineExerciseDTOs.all{ it.routineExerciseSetDTOs.all{ it2 -> it2.checked}}

    LaunchedEffect(allChecked) {
        onAllCheckedChange(allChecked)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        item {
            headerItem()
        }

        itemsIndexed(routinesState.selectedRoutineDTO.routineExerciseDTOs) { routineExerciseDTOIndex, routineExerciseDTO ->
            NewExerciseWidget(
                routinesViewModel = routinesViewModel,
                routinesState = routinesState,

                routinesExerciseDTOIndex = routineExerciseDTOIndex,

                editingEnabled = editingEnabled,
                checkboxEnabled = checkboxEnabled,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SleekButton(
                        text = stringResource(R.string.add_exercises),
                        onClick = {
                            navigationState.navController?.navigate(context.getString(R.string.add_exercises_route))
                        }
                    )

                    footerItem()
                }

            }

        }
    }
}

@Composable
fun NewExerciseWidget(
    routinesViewModel: RoutinesViewModel,
    routinesState: RoutinesState,
    routinesExerciseDTOIndex: Int,

    editingEnabled: Boolean,
    checkboxEnabled: Boolean,
) {
    val routineExerciseDTO =
        routinesState.selectedRoutineDTO.routineExerciseDTOs[routinesExerciseDTOIndex]

    var allChecked =
        routinesState.selectedRoutineDTO.routineExerciseDTOs[routinesExerciseDTOIndex].routineExerciseSetDTOs.all { it.checked }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = routineExerciseDTO.exercise.name,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.tertiary
                )
            )
            if (editingEnabled) {
                DeleteButton(
                    onClick = {
                        routinesViewModel.onEvent(
                            RoutinesEvent.RemoveRoutineExerciseDTO(
                                routineExerciseDTO
                            )
                        )
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }

        val firstColumnWidth = 0.1f
        val secondColumnWidth = 0.45f
        val thirdColumnWidth = 0.45f
        val extraColumnWidth: Dp = 20.dp

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
//            Text(
//                text = if (allChecked) "All items are checked!" else "Some items are unchecked",
//                style = MaterialTheme.typography.bodyLarge
//            )
            Row(
                modifier = Modifier
                    .height(20.dp)
                    .padding(0.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "#",
                    modifier = Modifier
                        .weight(firstColumnWidth),
                    style = MaterialTheme.typography.labelSmall.copy(
                        textAlign = TextAlign.Center
                    )
                )
                if (editingEnabled) {
                    Spacer(modifier = Modifier.width(extraColumnWidth))
                }
                SmallTextWidget(
                    text = stringResource(R.string.reps),
                    modifier = Modifier.weight(thirdColumnWidth),
                    textAlign = TextAlign.Center
                )
                SmallTextWidget(
                    text = stringResource(R.string.weight),
                    modifier = Modifier.weight(secondColumnWidth),
                    textAlign = TextAlign.Center
                )
                if (checkboxEnabled) {
                    Spacer(modifier = Modifier.width(extraColumnWidth))
                }
            }
            routineExerciseDTO.routineExerciseSetDTOs.forEachIndexed { routineExerciseSetIndex, routineExerciseSetDTO ->
                RoutineExerciseSetWidget(
                    routinesViewModel = routinesViewModel,
                    routinesState = routinesState,

                    routinesExerciseDTOIndex = routinesExerciseDTOIndex,
                    routineExerciseSetIndex = routineExerciseSetIndex,

                    editingEnabled = editingEnabled,
                    checkboxEnabled = checkboxEnabled,

                    firstColumnWidth = firstColumnWidth,
                    secondColumnWidth = secondColumnWidth,
                    thirdColumnWidth = thirdColumnWidth,
                    extraColumnWidth = extraColumnWidth,
                )
            }
            if (editingEnabled) {
                SleekButton(
                    text = stringResource(R.string.add_set),
                    onClick = {
                        routinesViewModel.onEvent(
                            RoutinesEvent.AddRoutineExerciseSet(
                                routineExerciseDTO.routineExercise
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun RoutineExerciseSetWidget(
    routinesViewModel: RoutinesViewModel,
    routinesState: RoutinesState,

    routinesExerciseDTOIndex: Int,
    routineExerciseSetIndex: Int,

    editingEnabled: Boolean,
    checkboxEnabled: Boolean,

    firstColumnWidth: Float,
    secondColumnWidth: Float,
    thirdColumnWidth: Float,
    extraColumnWidth: Dp,
) {

    val routineExerciseDTO =
        routinesState.selectedRoutineDTO.routineExerciseDTOs[routinesExerciseDTOIndex]
    val routineExerciseSetDTO = routineExerciseDTO.routineExerciseSetDTOs[routineExerciseSetIndex]

    var reps by remember(routineExerciseSetDTO) { mutableStateOf(routineExerciseSetDTO.reps.toString()) }
    var weight by remember(routineExerciseSetDTO) { mutableStateOf(routineExerciseSetDTO.weight.toString()) }

    var checked by remember(routineExerciseSetDTO) { mutableStateOf(routineExerciseSetDTO.checked) }

    var checkBoxEnabled by remember(routineExerciseSetDTO) { mutableStateOf(true) }
    var inputsEnabled by remember(routineExerciseSetDTO) { mutableStateOf(true) }

    LaunchedEffect(reps, weight) {
        checkBoxEnabled = reps.isNotEmpty() && reps != "0"
                && weight.isNotEmpty() && weight != "0"
    }

    LaunchedEffect(checked) {
        inputsEnabled = !checked
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (routineExerciseSetIndex + 1).toString(),
            modifier = Modifier
                .weight(firstColumnWidth),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center
            )
        )
        if (checkboxEnabled) {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    routinesViewModel.onEvent(
                        RoutinesEvent.UpdateRoutineExerciseSet(
                            routineExerciseSetDTO = routineExerciseSetDTO.copy(
                                checked = it,
                                tempId = routineExerciseSetDTO.tempId
                            ),
                            routineExercise = routineExerciseDTO.routineExercise
                        )
                    )
                    checked = it
                },
                modifier = Modifier
                    .width(extraColumnWidth),
                enabled = checkBoxEnabled
            )
        }
        CustomIntegerField(
            value = reps,
            onValueChange = {
                if (it != null) {
                    routinesViewModel.onEvent(
                        RoutinesEvent.UpdateRoutineExerciseSet(
                            routineExerciseSetDTO = routineExerciseSetDTO.copy(
                                routineExerciseSet = routineExerciseSetDTO.routineExerciseSet.copy(
                                    reps = it
                                ),
                                tempId = routineExerciseSetDTO.tempId
                            ),
                            routineExercise = routineExerciseDTO.routineExercise
                        )
                    )
                    reps = it.toString()
                } else {
                    reps = ""
                }
            },
            modifier = Modifier
                .weight(thirdColumnWidth),
            textStyle = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            ),
            insidePadding = PaddingValues(4.dp),
            enabled = inputsEnabled
        )
        CustomIntegerField(
            value = weight,
            onValueChange = {
                if (it != null) {
                    routinesViewModel.onEvent(
                        RoutinesEvent.UpdateRoutineExerciseSet(
                            routineExerciseSetDTO = routineExerciseSetDTO.copy(
                                routineExerciseSet = routineExerciseSetDTO.routineExerciseSet.copy(
                                    weight = it
                                ),
                                tempId = routineExerciseSetDTO.tempId
                            ),
                            routineExercise = routineExerciseDTO.routineExercise
                        )
                    )
                    weight = it.toString()
                } else {
                    weight = ""
                }
            },
            modifier = Modifier
                .weight(secondColumnWidth),
            textStyle = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            ),
            insidePadding = PaddingValues(4.dp),
            enabled = inputsEnabled
        )

        if (editingEnabled) {
            DeleteButton(
                onClick = {
                    routinesViewModel.onEvent(
                        RoutinesEvent.RemoveRoutineExerciseSet(
                            routineExerciseSetDTO = routineExerciseSetDTO,
                            routineExercise = routineExerciseDTO.routineExercise
                        )
                    )

                },
                modifier = Modifier
                    .width(extraColumnWidth),
            )
        }
    }
}
