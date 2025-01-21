package com.project.fitnessbuddy.screens.statistics

import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.api.auth.AuthViewModel
import com.project.fitnessbuddy.api.auth.UserState
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel,
    navigationViewModel: NavigationViewModel,
    userState: UserState,
    authViewModel: AuthViewModel
) {
    val userId = userState.user.userId ?: return
    val isAdmin = remember { authViewModel.hasRole(userState, "ROLE_ADMIN") }
    var adminView by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = remember {
        lifecycleOwner.lifecycleScope
    }
    val appOpenData by if (adminView && isAdmin) {
        statisticsViewModel.allAppOpenData.collectAsState()
    } else {
        statisticsViewModel.appOpenData.collectAsState()
    }

    val completedRoutinesData by if (adminView && isAdmin) {
        statisticsViewModel.allCompletedRoutinesData.collectAsState()
    } else {
        statisticsViewModel.completedRoutinesData.collectAsState()
    }
    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            navigationViewModel.onEvent(NavigationEvent.ClearTopBarActions)
            navigationViewModel.onEvent(NavigationEvent.DisableCustomButton)

            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                MediumTextWidget(context.getString(R.string.statistics))
            })
        }

        onDispose {
            job.cancel()
        }
    }
    LaunchedEffect(userId, adminView) {
        if (adminView && isAdmin) {
            statisticsViewModel.fetchAllAppOpenData()
            statisticsViewModel.fetchAllCompletedRoutines()
        } else {
            statisticsViewModel.fetchAppOpenData(userId)
            statisticsViewModel.fetchCompletedRoutines()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isAdmin) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.connected_as_admin),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Admin Role",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = adminView,
                    onCheckedChange = { adminView = it }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                RoundedChartBox {
                    AppOpenChart(
                        data = appOpenData,
                        title = if (adminView && isAdmin)
                            "${stringResource(R.string.weekly_app_open_statistics)} (All Users)"
                        else
                            stringResource(R.string.weekly_app_open_statistics)
                    )
                }
            }
            item {
                RoundedChartBox {
                    AppOpenChart(
                        data = completedRoutinesData,
                        title = if (adminView && isAdmin)
                            "${stringResource(R.string.completed_routines)} (All Users)"
                        else
                            stringResource(R.string.completed_routines)
                    )
                }
            }
        }
    }
}



@Composable
fun RoundedChartBox(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = shape,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        content()
    }
}

@Composable
fun AppOpenChart(
    data: Map<LocalDate, Int>,
    title: String
) {
    val daysOfWeek = List(7) { LocalDate.now().minusDays(6 - it.toLong()) }
    val completeData = daysOfWeek.associateWith { data[it] ?: 0 }

    val entries = completeData.entries.mapIndexed { index, (_, count) ->
        Entry(index.toFloat(), count.toFloat())
    }

    val onSecondaryContainerColor = MaterialTheme.colorScheme.onSecondaryContainer.hashCode()

    val dataSet = LineDataSet(entries, "App Opens").apply {
        lineWidth = 3f
        circleRadius = 6f
        setDrawCircleHole(true)
        setCircleColor(MaterialTheme.colorScheme.primary.hashCode())
        color = MaterialTheme.colorScheme.secondary.hashCode()
        setDrawValues(true)
        valueTextSize = 12f
        valueTextColor = onSecondaryContainerColor
        valueTypeface = Typeface.DEFAULT_BOLD
        valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
    }

    val lineData = LineData(dataSet)
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
            modifier = Modifier.padding(16.dp)
        )
    }
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                this.data = lineData
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                xAxis.apply {
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(completeData.keys.map { it.format(
                        DateTimeFormatter.ofPattern("dd MMM")) })
                    position = XAxis.XAxisPosition.BOTTOM
                    textSize = 12f
                    textColor = onSecondaryContainerColor
                    labelRotationAngle = 70f
                    yOffset = 10f
                }
                axisLeft.apply {
                    textSize = 12f
                    textColor = onSecondaryContainerColor
                    axisMinimum = 0f
                }
                axisRight.isEnabled = false
                legend.isEnabled = false
                description.isEnabled = false
                animateX(1000)
                animateY(1000)
            }
        },
        update = { chart ->
            chart.data = lineData
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(16.dp)
    )
}