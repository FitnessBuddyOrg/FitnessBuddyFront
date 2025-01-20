package com.project.fitnessbuddy.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.project.fitnessbuddy.api.auth.UserState
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel,
    statisticsState: StatisticsState,
    navigationViewModel: NavigationViewModel,
    userState: UserState,
) {
    val appOpenData by statisticsViewModel.appOpenData.collectAsState()
    val userId = userState.user.userId ?: return

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
                MediumTextWidget(context.getString(R.string.statistics))
            })
        }

        onDispose {
            job.cancel()
        }
    }

    LaunchedEffect(userId) {
        statisticsViewModel.fetchAppOpenData(userId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            RoundedChartBox {
                AppOpenChart(
                    data = appOpenData,
                    title = stringResource(R.string.weekly_app_open_statistics)
                )
            }
        }

        item {
            RoundedChartBox {
                AppOpenChart(
                    data = statisticsState.getCompletedRoutinesData(),
                    title = stringResource(R.string.completed_routines)
                )
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
                    valueFormatter = IndexAxisValueFormatter(completeData.keys.map { it.toString() })
                    position = XAxis.XAxisPosition.BOTTOM
                    textSize = 12f
                    textColor = onSecondaryContainerColor
                    labelRotationAngle = 70f
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
