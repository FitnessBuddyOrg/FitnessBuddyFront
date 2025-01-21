package com.project.fitnessbuddy.screens.statistics

import android.app.Application
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
}