package com.project.fitnessbuddy.screens.statistics

import android.app.AppOpsManager
import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.fitnessbuddy.api.auth.AuthViewModel
import com.project.fitnessbuddy.api.user.UserApi
import com.project.fitnessbuddy.database.dao.RoutineDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class StatisticsViewModel(
    application: Application,
    private val userApi: UserApi,
    private val routineDao: RoutineDao,
    private val authViewModel: AuthViewModel
) : AndroidViewModel(application) {

    private val _appOpenData = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val appOpenData: StateFlow<Map<LocalDate, Int>> = _appOpenData

    private val _completedRoutinesData = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val completedRoutinesData: StateFlow<Map<LocalDate, Int>> = _completedRoutinesData

    private val _allAppOpenData = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val allAppOpenData: StateFlow<Map<LocalDate, Int>> = _allAppOpenData

    private val _allCompletedRoutinesData = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val allCompletedRoutinesData: StateFlow<Map<LocalDate, Int>> = _allCompletedRoutinesData

    private val _timeSpentData = MutableStateFlow<Map<LocalDate, Long>>(emptyMap())
    val timeSpentData: StateFlow<Map<LocalDate, Long>> = _timeSpentData

    fun fetchAppOpenData(userId: Long) {
        viewModelScope.launch {
            try {
                val data = userApi.getAppOpenCount(userId)
                val startDate = LocalDateTime.now().minusWeeks(1)
                val groupedData = data
                    .map { it.getParsedOpenTime() to it }
                    .filter { (parsedTime, _) -> parsedTime.isAfter(startDate) }
                    .groupBy { (parsedTime, _) -> parsedTime.toLocalDate() }
                    .mapValues { it.value.size }
                _appOpenData.value = groupedData
            } catch (e: Exception) {
                println("Error fetching app open data: ${e.message}")
                _appOpenData.value = emptyMap()
            }
        }
    }

    fun fetchAllAppOpenData() {
        viewModelScope.launch {
            try {
                val data = userApi.getAllAppOpenCount()
                val groupedData = data
                    .map { it.getParsedOpenTime() to it }
                    .groupBy { (parsedTime, _) -> parsedTime.toLocalDate() }
                    .mapValues { it.value.size }
                _allAppOpenData.value = groupedData
            } catch (e: Exception) {
                println("Error fetching all app open data: ${e.message}")
                _allAppOpenData.value = emptyMap()
            }
        }
    }

    fun fetchCompletedRoutines() {
        viewModelScope.launch {
            try {
                val data = userApi.getCompletedRoutine()
                val startDate = LocalDateTime.now().minusWeeks(1)
                val groupedData = data
                    .map { it.getParsedCompletedTime() to it }
                    .filter { (parsedTime, _) -> parsedTime.isAfter(startDate) }
                    .groupBy { (parsedTime, _) -> parsedTime.toLocalDate() }
                    .mapValues { it.value.size }
                _completedRoutinesData.value = groupedData
            } catch (e: Exception) {
                println("Error fetching routine data: ${e.message}")
                _completedRoutinesData.value = emptyMap()
            }
        }
    }

    fun fetchAllCompletedRoutines() {
        viewModelScope.launch {
            try {
                val data = userApi.getAllCompletedRoutine()
                val groupedData = data
                    .map { it.getParsedCompletedTime() to it }
                    .groupBy { (parsedTime, _) -> parsedTime.toLocalDate() }
                    .mapValues { it.value.size }
                _allCompletedRoutinesData.value = groupedData
            } catch (e: Exception) {
                println("Error fetching all completed routines: ${e.message}")
                _allCompletedRoutinesData.value = emptyMap()
            }
        }
    }

    fun fetchTimeSpentData() {
        viewModelScope.launch {
            try {
                val usageStatsManager = getApplication<Application>().getSystemService(
                    UsageStatsManager::class.java)
                val endTime = System.currentTimeMillis()
                val startTime = endTime - (7 * 24 * 60 * 60 * 1000)
                val stats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
                )
                val groupedData = stats
                    .filter { it.packageName == getApplication<Application>().packageName }
                    .groupBy { LocalDate.ofEpochDay(it.firstTimeStamp / (24 * 60 * 60 * 1000)) }
                    .mapValues { entry -> entry.value.sumOf { it.totalTimeInForeground } / 3600000 }
                _timeSpentData.value = groupedData
            } catch (e: Exception) {
                println("Error fetching time spent data: ${e.message}")
                _timeSpentData.value = emptyMap()
            }
        }
    }

    fun requestUsageStatsPermission(context: Context) {
        if (!hasUsageStatsPermission(context)) {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

}