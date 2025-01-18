package com.project.fitnessbuddy.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class NavigationViewModel : ViewModel() {
    private val _state = MutableStateFlow(NavigationState())
    val state =
        _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NavigationState())

//    val state = combine(
//        _state,
//        _title
//    ) { state, title ->
//        state.copy(
//            title = title
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

            is NavigationEvent.UpdateTitleWidget -> {
                _state.update {
                    it.copy(
                        titleWidget = navigationEvent.titleWidget
                    )
                }
            }

            is NavigationEvent.SetBackButton -> {
                _state.update {
                    it.copy(
                        iconButton = {
                            BackButton(
                                navigationEvent.navController,
                                navigationEvent.onClick
                            )
                        }
                    )
                }
            }

            is NavigationEvent.DisableCustomButton -> {
                _state.update {
                    it.copy(
                        customButtonEnabled = false,
                    )
                }
            }

            is NavigationEvent.EnableCustomButton -> {
                _state.update {
                    it.copy(
                        customButtonEnabled = true
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
