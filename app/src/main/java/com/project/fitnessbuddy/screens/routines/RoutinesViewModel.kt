package com.project.fitnessbuddy.screens.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fitnessbuddy.database.dao.RoutineDao
import com.project.fitnessbuddy.database.dao.RoutineExerciseDao
import com.project.fitnessbuddy.database.dao.RoutineExerciseSetDao
import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.screens.exercises.ExercisesEvent
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
                        selectedRoutineDTO = routinesEvent.selectedRoutineDTO
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

            is RoutinesEvent.SaveRoutine -> {
                if (_state.value.selectedRoutineDTO.routine.name.isBlank()) {
                    return false
                }

                viewModelScope.launch {

                    // INSERT ROUTINE
                    _state.update {
                        it.copy(
                            selectedRoutineDTO = it.selectedRoutineDTO.copy(
                                routine = it.selectedRoutineDTO.routine.copy(
                                    routineId = routineDao.upsert(_state.value.selectedRoutineDTO.routine)
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
                                            routineId = _state.value.selectedRoutineDTO.routine.routineId!!
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
                                            routineExerciseId = routineExerciseDao.upsert(routineExerciseDTO.routineExercise)
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
                                        routineExerciseSets = routineExerciseDTO.routineExerciseSets.map { routineExerciseSet ->
                                            routineExerciseSet.copy(
                                                routineExerciseId = routineExerciseDTO.routineExercise.routineExerciseId!!
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
                                        routineExerciseSets = routineExerciseDTO.routineExerciseSets.map { routineExerciseSet ->
                                            routineExerciseSet.copy(
                                                routineExerciseSetId = routineExerciseSetDao.upsert(routineExerciseSet)
                                            )
                                        }
                                    )
                                }
                            )
                        )
                    }
                }

                RoutinesEvent.ResetSelectedRoutineDTO
            }

            is RoutinesEvent.UpdateRoutine -> {
                if (_state.value.selectedRoutineDTO.routine.name.isBlank()) {
                    return false
                }

                viewModelScope.launch {
                    routineDao.upsert(_state.value.selectedRoutineDTO.routine)
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

        }
        return true
    }
}
