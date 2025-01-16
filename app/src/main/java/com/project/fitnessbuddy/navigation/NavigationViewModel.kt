package com.project.fitnessbuddy.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class NavigationViewModel : ViewModel() {
    private val _title = MutableStateFlow("")
    private val _state = MutableStateFlow(NavigationState())

    val state = combine(
        _state,
        _title
    ) { state, title ->
        state.copy(
            title = title
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NavigationState())

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
                _title.value = navigationEvent.title
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
//
            is NavigationEvent.DisableAllButtons -> {
                _state.update {
                    it.copy(
                        backButtonEnabled = false,
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

            is NavigationEvent.AddTopBarActions -> {
                _state.update {
                    it.copy(
                        topBarActions = it.topBarActions + navigationEvent.topBarActions
                    )
                }
            }

            is NavigationEvent.ClearTopBarActions -> {
                _state.update {
                    it.copy(
                        topBarActions = emptyList()
                    )
                }
            }

        }
    }
}
