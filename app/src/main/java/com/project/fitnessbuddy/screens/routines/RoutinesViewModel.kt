package com.project.fitnessbuddy.screens.routines

import RetrofitInstance
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.project.fitnessbuddy.api.auth.AuthViewModel
import com.project.fitnessbuddy.database.dao.RoutineDao
import com.project.fitnessbuddy.database.dao.RoutineExerciseDao
import com.project.fitnessbuddy.database.dao.RoutineExerciseSetDao
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
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class RoutinesViewModel(
    application: Application,

    private val routineDao: RoutineDao,
    private val routineExerciseDao: RoutineExerciseDao,
    private val routineExerciseSetDao: RoutineExerciseSetDao,

    authViewModel: AuthViewModel

) : AndroidViewModel(application) {
    private val _searchValue = MutableStateFlow("")

    private val _templateRoutineDTOs = combine(
        _searchValue,
        authViewModel.userState
    ) { searchValue, userState ->
        if (userState.user.userId == null) {
            MutableStateFlow(emptyList())
        } else {
            routineDao.getTemplateRoutineDTOs(searchValue, userState.user.userId)
        }
    }
        .flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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

    private val _state = MutableStateFlow(RoutinesState())
    val state = combine(
        _state,
        _templateRoutineDTOs,
        _completedRoutineDTOs,
        _searchValue
    ) { state, templateRoutineDTOs, completedRoutineDTOs, searchValue ->
        state.copy(
            templateRoutineDTOs = templateRoutineDTOs,
            completedRoutineDTOs = completedRoutineDTOs,
            searchValue = searchValue,
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

            is RoutinesEvent.SetEditType -> {
                _state.update {
                    it.copy(
                        editType = routinesEvent.editType
                    )
                }
            }

            is RoutinesEvent.DeleteRoutine -> {
                viewModelScope.launch {
                    routineDao.delete(routinesEvent.routine)
                }
            }

            is RoutinesEvent.UpsertSelectedRoutineDTO -> {
                if (_state.value.selectedRoutineDTO.routine.name.isBlank() ||
                    _state.value.selectedRoutineDTO.routineExerciseDTOs.isEmpty()
                ) {
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

            is RoutinesEvent.UpsertRoutineDTO -> {
                var routineDTO = routinesEvent.routineDTO

                if (routineDTO.routine.name.isBlank() ||
                    routineDTO.routineExerciseDTOs.isEmpty()
                ) {
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
                    routineDao.delete(routineDTO.routine)

                    // INSERT ROUTINE
                    routineDTO = routineDTO.copy(
                        routine = routineDTO.routine.copy(
                            routineId = checkId(
                                routineDao.upsert(routineDTO.routine),
                                routineDTO.routine.routineId
                            )
                        )
                    )

                    // SET ROUTINE ID FOR EVERY ROUTINE EXERCISE
                    routineDTO = routineDTO.copy(
                        routineExerciseDTOs = routineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
                            routineExerciseDTO.copy(
                                routineExercise = routineExerciseDTO.routineExercise.copy(
                                    routineId = routineDTO.routine.routineId
                                )
                            )
                        }
                    )

                    // INSERT ROUTINE EXERCISES
                    routineDTO = routineDTO.copy(
                        routineExerciseDTOs = routineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
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

                    // SET ROUTINE EXERCISE ID FOR EVERY ROUTINE EXERCISE SET
                    routineDTO = routineDTO.copy(
                        routineExerciseDTOs = routineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
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

                    // INSERT ROUTINE EXERCISE SETS
                    routineDTO = routineDTO.copy(
                        routineExerciseDTOs = routineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
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

            is RoutinesEvent.SetStartDate -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routine = it.selectedRoutineDTO.routine.copy(
                                startDate = routinesEvent.startDate
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

            is RoutinesEvent.CompleteRoutine -> {
                _state.update {
                    it.copy(
                        selectedRoutineDTO = it.selectedRoutineDTO.copy(
                            routine = it.selectedRoutineDTO.routine.copy(
                                lastPerformed = Date()
                            )
                        )
                    )
                }
                viewModelScope.launch {
                    RetrofitInstance.userApi.addCompletedRoutine()
                }
                onEvent(RoutinesEvent.UpsertSelectedRoutineDTO)


                val completedRoutineDTO = _state.value.selectedRoutineDTO.copy(
                    routine = _state.value.selectedRoutineDTO.routine.copy(
                        routineId = null,
                        endDate = Date(),
                        lastPerformed = null,
                        isCompleted = true
                    ),
                    routineExerciseDTOs = _state.value.selectedRoutineDTO.routineExerciseDTOs.map { routineExerciseDTO ->
                        routineExerciseDTO.copy(
                            routineExercise = routineExerciseDTO.routineExercise.copy(
                                routineExerciseId = null,
                                routineId = null
                            ),
                            routineExerciseSetDTOs = routineExerciseDTO.routineExerciseSetDTOs.map { routineExerciseSetDTO ->
                                routineExerciseSetDTO.copy(
                                    routineExerciseSet = routineExerciseSetDTO.routineExerciseSet.copy(
                                        routineExerciseSetId = null,
                                        routineExerciseId = null
                                    )
                                )
                            }
                        )
                    }
                )

                onEvent(RoutinesEvent.UpsertRoutineDTO(completedRoutineDTO))
            }
        }
        return true

    }

    fun sendRoutineUpdate(context: Context) {
        val routineDTOJson = Gson().toJson(_state.value.selectedRoutineDTO)

        val intent = Intent(context, StartRoutineServiceNotification::class.java).apply {
            putExtra(SELECTED_ROUTINE_DTO, routineDTOJson)
            action = ROUTINE_UPDATE
        }

        println("sending routine: " + _state.value.selectedRoutineDTO)

        context.startForegroundService(intent)
    }
}


