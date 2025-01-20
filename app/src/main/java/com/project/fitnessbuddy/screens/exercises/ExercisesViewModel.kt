package com.project.fitnessbuddy.screens.exercises

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.fitnessbuddy.api.auth.AuthViewModel
import com.project.fitnessbuddy.api.user.ShareExerciseDTO
import com.project.fitnessbuddy.api.user.TokenResponseDTO
import com.project.fitnessbuddy.database.dao.ExerciseDao
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.enums.Category
import com.project.fitnessbuddy.database.entity.enums.CustomState
import com.project.fitnessbuddy.database.entity.enums.Language
import com.project.fitnessbuddy.database.entity.enums.ShareType
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
    application: Application,

    private val exerciseDao: ExerciseDao,

    private val authViewModel: AuthViewModel
) : AndroidViewModel(application) {
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _searchValue = MutableStateFlow("")

    private val appContext = application.applicationContext

    private val _exercises = combine(
        _sortType,
        _searchValue,
        authViewModel.userState
    ) { sortType, searchValue, userState ->
        if (userState.user.userId == null) {
            MutableStateFlow(emptyList())
        } else {
            when (sortType) {
                SortType.NAME -> exerciseDao.getExercisesOrderedByName(
                    searchValue,
                    userState.user.userId
                )
                SortType.CATEGORY -> exerciseDao.getExercisesOrderedByCategory(
                    searchValue,
                    userState.user.userId
                )
            }
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

            is ExercisesEvent.SetEditType -> {
                _state.update {
                    it.copy(
                        editType = exercisesEvent.editType
                    )
                }
            }

            is ExercisesEvent.ShareSelectedExercise -> {
                val sharedExerciseDTO = ShareExerciseDTO(
                    name = _state.value.selectedExercise.name,
                    instructions = _state.value.selectedExercise.instructions,
                    videoLink = _state.value.selectedExercise.videoLink,
                    category = _state.value.selectedExercise.category
                )

                viewModelScope.launch {
                    println(sharedExerciseDTO)
                    val tokenResponseDTO: TokenResponseDTO = RetrofitInstance.exerciseApi.shareExercise(sharedExerciseDTO)

                    _state.update {
                        it.copy (
                            sharedExerciseToken = tokenResponseDTO.token
                        )
                    }
                }
            }

            is ExercisesEvent.ClearSharedExerciseToken -> {
                _state.update {
                    it.copy(
                        sharedExerciseToken = ""
                    )
                }
            }

            is ExercisesEvent.SetFetchedExerciseToken -> {
                _state.update {
                    it.copy(
                        fetchedExerciseToken = exercisesEvent.fetchedExerciseToken
                    )
                }
            }

            is ExercisesEvent.FetchExerciseByToken -> {
                viewModelScope.launch {
                    try {
                        val sharedExerciseDTO: ShareExerciseDTO = RetrofitInstance.exerciseApi.getSharedExercise(_state.value.fetchedExerciseToken)

                        _state.update {
                            it.copy(
                                selectedExercise = Exercise(
                                    name = sharedExerciseDTO.name ?: "",
                                    instructions = sharedExerciseDTO.instructions ?: "",
                                    videoLink = sharedExerciseDTO.videoLink ?: "",
                                    category = sharedExerciseDTO.category ?: Category.ARMS,
                                    shareType = ShareType.PRIVATE,
                                    language = Language.ENGLISH
                                ),
                                exerciseFetched = CustomState.TRUE
                            )
                        }
                    } catch(e: Exception) {
                        _state.update {
                            it.copy(
                                exerciseFetched = CustomState.FALSE
                            )
                        }
                    }
                }
            }

            is ExercisesEvent.ResetFetching -> {
                _state.update {
                    it.copy(
                        exerciseFetched = CustomState.NONE,
                        fetchedExerciseToken = ""
                    )
                }
                onEvent(ExercisesEvent.SetSelectedExercise(Exercise()))
            }

            is ExercisesEvent.DeleteExercise -> {
                viewModelScope.launch {
                    exerciseDao.delete(exercisesEvent.exercise)
                }
            }

            is ExercisesEvent.UpsertExercise -> {
                if (_state.value.selectedExercise.name.isBlank()) {
                    return false
                } else {
                    _state.update {
                        it.copy(
                            selectedExercise = _state.value.selectedExercise.copy(
                                userId = authViewModel.userState.value.user.userId
                            )
                        )
                    }
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
