package com.project.fitnessbuddy.database.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.project.fitnessbuddy.database.entity.Routine
import com.project.fitnessbuddy.database.entity.RoutineExercise
import com.project.fitnessbuddy.database.entity.abstracts.ListedEntity

data class RoutineDTO(
    @Embedded var routine: Routine = Routine(),

    @Relation(
        parentColumn = "routine_id",
        entityColumn = "routine_id",
        entity = RoutineExercise::class
    )
    val routineExerciseDTOs: List<RoutineExerciseDTO> = emptyList()
) : ListedEntity {
    fun getLastPerformed(): String {
        return routine.lastPerformed?.toString() ?: ""
    }

    override val name get() = routine.name
    override val language get() = routine.language
    override val shareType get() = routine.shareType
}
