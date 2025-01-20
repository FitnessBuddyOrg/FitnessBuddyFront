package com.project.fitnessbuddy.screens.statistics

import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.screens.common.toLocalDate
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

data class StatisticsState(
    val completedRoutinesData: List<RoutineDTO> = emptyList()
) {
    fun getCompletedRoutinesData(): Map<LocalDate, Int> {
        val startDate = LocalDateTime.now().minusWeeks(1)

        return completedRoutinesData
            .filter { it.routine.endDate?.toLocalDate()?.isAfter(startDate.toLocalDate()) == true }
            .groupBy {
                if(it.routine.endDate != null) {
                    it.routine.endDate!!.toLocalDate()
                } else {
                    Date().toLocalDate()
                }
            }
            .mapValues { it.value.size }
    }
}
