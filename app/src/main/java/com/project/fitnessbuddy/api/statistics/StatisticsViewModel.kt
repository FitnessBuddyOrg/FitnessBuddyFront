package com.project.fitnessbuddy.api.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fitnessbuddy.api.user.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class StatisticsViewModel(private val userApi: UserApi) : ViewModel() {

    private val _appOpenData = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val appOpenData: StateFlow<Map<LocalDate, Int>> = _appOpenData

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
}
