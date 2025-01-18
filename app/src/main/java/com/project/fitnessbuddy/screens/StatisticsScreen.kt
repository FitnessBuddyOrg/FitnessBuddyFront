package com.project.fitnessbuddy.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.project.fitnessbuddy.api.auth.UserState
import com.project.fitnessbuddy.api.statistics.StatisticsViewModel
import java.time.LocalDate

@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel,
    userState: UserState,
) {
    val appOpenData by statisticsViewModel.appOpenData.collectAsState()
    val userId = userState.id ?: return

    LaunchedEffect(userId) {
        statisticsViewModel.fetchAppOpenData(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        RoundedChartBox {
            AppOpenChart(appOpenData)
        }
    }
}

@Composable
fun RoundedChartBox(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = Color.White,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = shape,
        colors = CardDefaults.cardColors(backgroundColor),
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        content()
    }
}

@Composable
fun AppOpenChart(data: Map<LocalDate, Int>) {
    val daysOfWeek = List(7) { LocalDate.now().minusDays(6 - it.toLong()) }
    val completeData = daysOfWeek.associateWith { data[it] ?: 0 }

    val entries = completeData.entries.mapIndexed { index, (_, count) ->
        Entry(index.toFloat(), count.toFloat())
    }

    val dataSet = LineDataSet(entries, "App Opens").apply {
        lineWidth = 3f
        circleRadius = 6f
        setDrawCircleHole(true)
        setCircleColor(0xFFbc5090.toInt())
        color = 0xFF58508d.toInt()
        setDrawValues(true)
        valueTextSize = 12f
        valueTextColor = android.graphics.Color.BLACK
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
            text = "Weekly App Open Statistics",
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
                    textColor = android.graphics.Color.BLACK
                    labelRotationAngle = 70f
                }
                axisLeft.apply {
                    textSize = 12f
                    textColor = android.graphics.Color.BLACK
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
