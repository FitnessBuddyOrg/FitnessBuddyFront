package com.project.fitnessbuddy.screens.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.fitnessbuddy.api.auth.AuthViewModel
import com.project.fitnessbuddy.api.user.UserApi
import com.project.fitnessbuddy.database.dao.RoutineDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class StatisticsViewModel(
    application: Application,

    private val userApi: UserApi,
    private val routineDao: RoutineDao,

    authViewModel: AuthViewModel
) : AndroidViewModel(application) {

    private val _appOpenData = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val appOpenData: StateFlow<Map<LocalDate, Int>> = _appOpenData

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _completedRoutineDTOs = combine(
        authViewModel.userState
    ) { (userState) ->
        if (userState.user.userId == null) {
            MutableStateFlow(emptyList())
        } else {
            routineDao.getCompletedRoutineDTOs(userState.user.userId)
        }
    }
        .flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(StatisticsState())
    val state = combine(
        _state,
        _completedRoutineDTOs
    ) { state, completedRoutinesData ->
        state.copy(
            completedRoutinesData = completedRoutinesData
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatisticsState())

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

    fun onEvent(statisticsEvent: StatisticsEvent): Boolean {
//        when (statisticsEvent) {
//            is StatisticsEvent.FetchAppOpenData -> {
//
//            }
//        }
        return false
    }
}
