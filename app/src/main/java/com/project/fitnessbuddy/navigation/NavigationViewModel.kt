package com.project.fitnessbuddy.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class NavigationViewModel : ViewModel() {

    private val _state = MutableStateFlow(NavigationState())
    val state = _state
//    val state = combine(_state) { state ->
//        state.copy(
//            searchValue = searchValue
//        )
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NavigationState())
    fun onEvent(navigationEvent: NavigationEvent) {
        when (navigationEvent) {
            is NavigationEvent.SetNavController -> {
                _state.update {
                    it.copy(
                        navController = navigationEvent.navController
                    )
                }
            }

            is NavigationEvent.SetTitle -> {
                _state.update {
                    it.copy(
                        title = navigationEvent.title
                    )
                }
            }

            is NavigationEvent.UpdateTitleWidget -> {
                _state.update {
                    it.copy(
                        titleWidget = navigationEvent.titleWidget
                    )
                }
            }

//            is NavigationEvent.SetSearchValue -> {
//                _state.update {
//                    it.copy(
//                        searchValue = navigationEvent.searchValue
//                    )
//                }
//            }
//
//            is NavigationEvent.ClearSearchValue -> {
//                _state.update {
//                    it.copy(
//                        searchValue = ""
//                    )
//                }
//            }

            is NavigationEvent.DisableAllButtons -> {
                _state.update {
                    it.copy(
                        backButtonEnabled = false,
                        searchButtonEnabled = false,
                        addButtonEnabled = false,
                        deleteButtonEnabled = false,
                        editButtonEnabled = false,
                    )
                }
            }

            is NavigationEvent.EnableBackButton -> {
                _state.update {
                    it.copy(
                        backButtonEnabled = true
                    )
                }
            }

            is NavigationEvent.DisableBackButton -> {
                _state.update {
                    it.copy(
                        backButtonEnabled = false
                    )
                }
            }

            is NavigationEvent.EnableSearchButton -> {
                _state.update {
                    it.copy(
                        searchButtonEnabled = true
                    )
                }
            }

            is NavigationEvent.DisableSearchButton -> {
                _state.update {
                    it.copy(
                        searchButtonEnabled = false
                    )
                }
            }

            is NavigationEvent.EnableAddButton -> {
                _state.update {
                    it.copy(
                        addButtonEnabled = true
                    )
                }
            }

            is NavigationEvent.DisableAddButton -> {
                _state.update {
                    it.copy(
                        addButtonEnabled = false
                    )
                }
            }

            is NavigationEvent.SetAddButtonRoute -> {
                _state.update {
                    it.copy(
                        addButtonRoute = navigationEvent.addButtonRoute
                    )
                }
            }

            is NavigationEvent.EnableDeleteButton -> {
                _state.update {
                    it.copy(
                        deleteButtonEnabled = true
                    )
                }
            }

            is NavigationEvent.DisableDeleteButton -> {
                _state.update {
                    it.copy(
                        deleteButtonEnabled = false
                    )
                }
            }

            is NavigationEvent.SetOnDeleteButtonClicked -> {
                _state.update {
                    it.copy(
                        onDeleteButtonClicked = navigationEvent.onDeleteButtonClicked
                    )
                }
            }

            is NavigationEvent.EnableEditButton -> {
                _state.update {
                    it.copy(
                        editButtonEnabled = true
                    )
                }
            }

            is NavigationEvent.DisableEditButton -> {
                _state.update {
                    it.copy(
                        editButtonEnabled = false
                    )
                }
            }

            is NavigationEvent.SetEditButtonRoute -> {
                _state.update {
                    it.copy(
                        editButtonRoute = navigationEvent.editButtonRoute
                    )
                }
            }

        }
    }
}
