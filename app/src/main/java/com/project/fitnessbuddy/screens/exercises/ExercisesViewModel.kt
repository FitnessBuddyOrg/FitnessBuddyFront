package com.project.fitnessbuddy.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fitnessbuddy.database.dao.ExerciseDao
import com.project.fitnessbuddy.database.entity.Exercise
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ExercisesViewModel(
    private val exerciseDao: ExerciseDao,
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _searchValue = MutableStateFlow("")

    private val _exercises = combine(
        _sortType,
        _searchValue
    ) { sortType, searchValue ->
        when (sortType) {
            SortType.NAME -> exerciseDao.getExercisesOrderedByName(searchValue)
            SortType.CATEGORY -> exerciseDao.getExercisesOrderedByCategory(searchValue)
        }
    }
        .flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    private val _state = MutableStateFlow(ExercisesState())
    val state = combine(
        _state,
        _sortType,
        _exercises,
        _searchValue
    ) { state, sortType, exercises, searchValue ->
        state.copy(
            exercises = exercises,
            sortType = sortType,
            searchValue = searchValue
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExercisesState())


    fun onEvent(exercisesEvent: ExercisesEvent): Boolean {
        when (exercisesEvent) {
            is ExercisesEvent.SortExercises -> _sortType.value = exercisesEvent.sortType
            is ExercisesEvent.SetSearchValue -> _searchValue.value = exercisesEvent.searchValue

            is ExercisesEvent.SetSelectedExercise -> {
                _state.update {
                    it.copy(
                        selectedExercise = exercisesEvent.selectedExercise
                    )
                }
            }

            is ExercisesEvent.ResetSelectedExercise -> {
                _state.update {
                    it.copy(
                        selectedExercise = Exercise()
                    )
                }
            }

            is ExercisesEvent.SetEditType -> {
                _state.update {
                    it.copy(
                        editType = exercisesEvent.editType
                    )
                }
            }

            is ExercisesEvent.DeleteExercise -> {
                viewModelScope.launch {
                    exerciseDao.delete(exercisesEvent.exercise)
                }
            }

            is ExercisesEvent.SaveExercise -> {
                if (_state.value.selectedExercise.name.isBlank()) {
                    return false
                }

                viewModelScope.launch {
                    exerciseDao.upsert(_state.value.selectedExercise)
                }

                ExercisesEvent.ResetSelectedExercise
            }

            is ExercisesEvent.UpdateExercise -> {
                if (_state.value.selectedExercise.name.isBlank()) {
                    return false
                }

                viewModelScope.launch {
                    exerciseDao.upsert(_state.value.selectedExercise)
                }
            }

            is ExercisesEvent.SetName -> {
                _state.update {
                    it.copy(
                        selectedExercise = it.selectedExercise.copy(
                            name = exercisesEvent.name
                        )
                    )
                }
            }

            is ExercisesEvent.SetInstructions -> {
                _state.update {
                    it.copy(
                        selectedExercise = it.selectedExercise.copy(
                            instructions = exercisesEvent.instructions
                        )
                    )
                }
            }

            is ExercisesEvent.SetVideoLink -> {
                _state.update {
                    it.copy(
                        selectedExercise = it.selectedExercise.copy(
                            videoLink = exercisesEvent.videoLink
                        )
                    )
                }
            }

            is ExercisesEvent.SetCategory -> {
                _state.update {
                    it.copy(
                        selectedExercise = it.selectedExercise.copy(
                            category = exercisesEvent.category
                        )
                    )
                }
            }

            is ExercisesEvent.SetShareType -> {
                _state.update {
                    it.copy(
                        selectedExercise = it.selectedExercise.copy(
                            shareType = exercisesEvent.shareType
                        )
                    )
                }
            }

            is ExercisesEvent.SetLanguage -> {
                _state.update {
                    it.copy(
                        selectedExercise = it.selectedExercise.copy(
                            language = exercisesEvent.language
                        )
                    )
                }
            }

        }
        return true
    }
}
