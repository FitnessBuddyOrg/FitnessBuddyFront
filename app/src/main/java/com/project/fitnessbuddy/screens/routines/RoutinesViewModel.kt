package com.project.fitnessbuddy.screens.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fitnessbuddy.database.dao.RoutineDao
import com.project.fitnessbuddy.database.dao.RoutineExerciseDao
import com.project.fitnessbuddy.database.dao.RoutineExerciseSetDao
import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.database.dto.RoutineExerciseDTO
import com.project.fitnessbuddy.database.dto.RoutineExerciseSetDTO
import com.project.fitnessbuddy.database.entity.RoutineExercise
import com.project.fitnessbuddy.database.entity.RoutineExerciseSet
import com.project.fitnessbuddy.navigation.EditType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RoutinesViewModel(
    private val routineDao: RoutineDao,
    private val routineExerciseDao: RoutineExerciseDao,
    private val routineExerciseSetDao: RoutineExerciseSetDao
) : ViewModel() {
    private val _searchValue = MutableStateFlow("")

    private val _routineDTOs = combine(
        _searchValue
    ) { (searchValue) ->
        routineDao.getRoutineDTOs(searchValue)
    }
        .flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(RoutinesState())
    val state = combine(
        _state,
        _routineDTOs,
        _searchValue
    ) { state, routineDTOs, searchValue ->
        state.copy(
            routineDTOs = routineDTOs,
            searchValue = searchValue
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoutinesState())


    fun onEvent(routinesEvent: RoutinesEvent): Boolean {
        when (routinesEvent) {
            is RoutinesEvent.SetSearchValue -> _searchValue.value = routinesEvent.searchValue

            is RoutinesEvent.SetSelectedRoutineDTO -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = routinesEvent.routineDTO
                    )
                }
            }

            is RoutinesEvent.ResetSelectedRoutineDTO -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = RoutineDTO()
                    )
                }
            }

            is RoutinesEvent.SetEditType -> {
                _state.update {
                    it.copy(
                        editType = routinesEvent.editType
                    )
                }
            }

            is RoutinesEvent.DeleteRoutineDTO -> {
                viewModelScope.launch {
                    routineDao.delete(routinesEvent.routineDTO)
                }
            }

            is RoutinesEvent.UpsertRoutine -> {
                if (_state.value.selectedRoutineDTO.routine.name.isBlank()) {
                    return false
                }

                fun checkId(newId: Long, oldId: Long?): Long {
                    return if (newId == -1L && oldId != null) {
                        oldId
                    } else {
                        newId
                    }
                }

                viewModelScope.launch {
                    // DELETE EXISTING ROUTINE
                    if (_state.value.editType == EditType.EDIT) {
                        routineDao.delete(_state.value.selectedRoutineDTO.routine)
                    }

                    // INSERT ROUTINE
                    _state.update {
                        it.copy(
                            selectedRoutineDTO = it.selectedRoutineDTO.copy(
                                routine = it.selectedRoutineDTO.routine.copy(
                                    routineId = checkId(
                                        routineDao.upsert(_state.value.selectedRoutineDTO.routine),
                                        it.selectedRoutineDTO.routine.routineId
                                    )
                                )
                            )
                        )
                    }


                    // SET ROUTINE ID FOR EVERY ROUTINE EXERCISE
                    _state.update {
                        it.copy(
                            selectedRoutineDTO = it.selectedRoutineDTO.copy(
                                routineExerciseDTOs = it.selectedRoutineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
                                    routineExerciseDTO.copy(
                                        routineExercise = routineExerciseDTO.routineExercise.copy(
                                            routineId = _state.value.selectedRoutineDTO.routine.routineId
                                        )
                                    )
                                }
                            )
                        )
                    }

                    // INSERT ROUTINE EXERCISES
                    _state.update {
                        it.copy(
                            selectedRoutineDTO = it.selectedRoutineDTO.copy(
                                routineExerciseDTOs = it.selectedRoutineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
                                    routineExerciseDTO.copy(
                                        routineExercise = routineExerciseDTO.routineExercise.copy(
                                            routineExerciseId = checkId(
                                                routineExerciseDao.upsert(routineExerciseDTO.routineExercise),
                                                routineExerciseDTO.routineExercise.routineExerciseId
                                            )
                                        )
                                    )
                                }
                            )
                        )
                    }

                    // SET ROUTINE EXERCISE ID FOR EVERY ROUTINE EXERCISE SET
                    _state.update {
                        it.copy(
                            selectedRoutineDTO = it.selectedRoutineDTO.copy(
                                routineExerciseDTOs = it.selectedRoutineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
                                    routineExerciseDTO.copy(
                                        routineExerciseSetDTOs = routineExerciseDTO.routineExerciseSetDTOs.map { routineExerciseSetDTO ->
                                            routineExerciseSetDTO.copy(
                                                routineExerciseSet = routineExerciseSetDTO.routineExerciseSet.copy(
                                                    routineExerciseId = routineExerciseDTO.routineExercise.routineExerciseId!!
                                                )
                                            )
                                        }
                                    )
                                }
                            )
                        )
                    }

                    // INSERT ROUTINE EXERCISE SETS
                    _state.update {
                        it.copy(
                            selectedRoutineDTO = it.selectedRoutineDTO.copy(
                                routineExerciseDTOs = it.selectedRoutineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
                                    routineExerciseDTO.copy(
                                        routineExerciseSetDTOs = routineExerciseDTO.routineExerciseSetDTOs.map { routineExerciseSetDTO ->
                                            routineExerciseSetDTO.copy(
                                                routineExerciseSet = routineExerciseSetDTO.routineExerciseSet.copy(
                                                    routineExerciseSetId = checkId(
                                                        routineExerciseSetDao.upsert(
                                                            routineExerciseSetDTO.routineExerciseSet
                                                        ),
                                                        routineExerciseSetDTO.routineExerciseSet.routineExerciseSetId
                                                    )
                                                )
                                            )
                                        }
                                    )
                                }
                            )
                        )
                    }
                }
            }

            is RoutinesEvent.SetName -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routine = it.selectedRoutineDTO.routine.copy(
                                name = routinesEvent.name
                            )
                        )
                    )
                }
            }

            is RoutinesEvent.SetFrequency -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routine = it.selectedRoutineDTO.routine.copy(
                                frequency = routinesEvent.frequency
                            )
                        )
                    )
                }
            }

            is RoutinesEvent.SetShareType -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routine = it.selectedRoutineDTO.routine.copy(
                                shareType = routinesEvent.shareType
                            )
                        )
                    )
                }
            }

            is RoutinesEvent.SetLanguage -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routine = it.selectedRoutineDTO.routine.copy(
                                language = routinesEvent.language
                            )
                        )
                    )
                }
            }

            is RoutinesEvent.HandleExercise -> {
                val exerciseExistsInObject =
                    _state.value.selectedRoutineDTO.routineExerciseDTOs.find {
                        it.routineExercise.exerciseId == routinesEvent.exercise.exerciseId
                    } != null

                if (exerciseExistsInObject) {
                    if (routinesEvent.selected) {
                        _state.value.existingExercisesToRemove.remove(routinesEvent.exercise)

                    } else {
                        _state.value.existingExercisesToRemove.add(routinesEvent.exercise)
                    }
                } else {
                    if (routinesEvent.selected) {
                        _state.value.potentialExercisesToAdd.add(routinesEvent.exercise)
                    } else {
                        _state.value.potentialExercisesToAdd.remove(routinesEvent.exercise)
                    }
                }
            }


            is RoutinesEvent.ApplyExercises -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routineExerciseDTOs = it.selectedRoutineDTO.routineExerciseDTOs + it.potentialExercisesToAdd.map { exercise ->
                                RoutineExerciseDTO(
                                    routineExercise = RoutineExercise(
                                        routineId = it.selectedRoutineDTO.routine.routineId,
                                        exerciseId = exercise.exerciseId!!
                                    ),
                                    exercise = exercise,
                                    routineExerciseSetDTOs = listOf(
                                        RoutineExerciseSetDTO(
                                            routineExerciseSet = RoutineExerciseSet(
                                                weight = 0,
                                                reps = 0
                                            )
                                        )
                                    )
                                )
                            }
                        )
                    )
                }

                for (exercise in _state.value.existingExercisesToRemove) {
                    val routineExerciseDTO: RoutineExerciseDTO? =
                        _state.value.selectedRoutineDTO.routineExerciseDTOs.find {
                            it.routineExercise.exerciseId == exercise.exerciseId
                        }
                    if (routineExerciseDTO != null) {
                        _state.update {
                            it.copy(
                                selectedRoutineDTO = it.selectedRoutineDTO.copy(
                                    routineExerciseDTOs = it.selectedRoutineDTO.routineExerciseDTOs - routineExerciseDTO
                                )
                            )
                        }
                    }
                }

                onEvent(RoutinesEvent.ClearExercisesLists)
            }

            is RoutinesEvent.ClearExercisesLists -> {
                _state.value.potentialExercisesToAdd.clear()
                _state.value.existingExercisesToRemove.clear()
            }

            is RoutinesEvent.AddRoutineExerciseSet -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routineExerciseDTOs = it.selectedRoutineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
                                if (routineExerciseDTO.routineExercise.exerciseId == routinesEvent.routineExercise.exerciseId) {
                                    val newRoutineExerciseDTO = RoutineExerciseSetDTO(
                                        routineExerciseSet = RoutineExerciseSet(
                                            weight = 0,
                                            reps = 0
                                        )
                                    )

                                    println("created temp id: ${newRoutineExerciseDTO.tempId}")

                                    routineExerciseDTO.copy(
                                        routineExerciseSetDTOs = routineExerciseDTO.routineExerciseSetDTOs + newRoutineExerciseDTO
                                    )
                                } else {
                                    routineExerciseDTO
                                }
                            }
                        )
                    )
                }

            }

            is RoutinesEvent.RemoveRoutineExerciseDTO -> {
                println("going to remove exercise: ${routinesEvent.routineExerciseDTO.exercise}")
                println("going to remove sets: ${routinesEvent.routineExerciseDTO.routineExerciseSetDTOs}")
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routineExerciseDTOs = it.selectedRoutineDTO.routineExerciseDTOs - routinesEvent.routineExerciseDTO
                        )
                    )
                }
            }

            is RoutinesEvent.RemoveRoutineExerciseSet -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routineExerciseDTOs = it.selectedRoutineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
                                if (routineExerciseDTO.routineExercise.exerciseId == routinesEvent.routineExercise.exerciseId) {
                                    routineExerciseDTO.copy(
                                        routineExerciseSetDTOs = routineExerciseDTO.routineExerciseSetDTOs - routinesEvent.routineExerciseSetDTO
                                    )
                                } else {
                                    routineExerciseDTO
                                }
                            }
                        )
                    )
                }

                val routineExerciseDTO = _state.value.selectedRoutineDTO.routineExerciseDTOs.find {
                    it.routineExercise.exerciseId == routinesEvent.routineExercise.exerciseId
                }

                if (routineExerciseDTO?.routineExerciseSetDTOs.isNullOrEmpty()) {
                    routineExerciseDTO?.let {
                        onEvent(RoutinesEvent.RemoveRoutineExerciseDTO(it))
                    }
                }
            }

            is RoutinesEvent.UpdateRoutineExerciseSet -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routineExerciseDTOs = it.selectedRoutineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
                                if (routineExerciseDTO.routineExercise.exerciseId == routinesEvent.routineExercise.exerciseId) {
                                    routineExerciseDTO.copy(
                                        routineExerciseSetDTOs = routineExerciseDTO.routineExerciseSetDTOs.map { routineExerciseSetDTO ->
                                            if (routineExerciseSetDTO.tempId == routinesEvent.routineExerciseSetDTO.tempId) {
                                                routinesEvent.routineExerciseSetDTO
                                            } else {
                                                routineExerciseSetDTO
                                            }
                                        }
                                    )
                                } else {
                                    routineExerciseDTO
                                }
                            }
                        )
                    )
                }
            }
        }
        return true

    }
}


