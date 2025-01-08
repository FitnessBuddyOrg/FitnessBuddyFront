package com.project.fitnessbuddy.screens.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fitnessbuddy.database.dao.ParameterDao
import com.project.fitnessbuddy.database.entity.Parameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ParametersViewModel(
    private val parameterDao: ParameterDao
) : ViewModel() {
    private val _state = MutableStateFlow(ParametersState())
    val state = _state

    fun onEvent(parametersEvent: ParametersEvent): Boolean {
        when (parametersEvent) {
            is ParametersEvent.InitializeParameters -> {
                viewModelScope.launch {
                    val languageParameter = parameterDao.getParameterById(LANGUAGE_ID)

                    if(languageParameter != null) {
                        _state.update {
                            it.copy(
                                languageParameter = languageParameter.copy(
                                    value = languageParameter.value
                                )
                            )
                        }
                    } else {
                        println("Using default language ${state.value.languageParameter.value}")
                    }

                }
            }

            is ParametersEvent.SetLanguageParameterValue -> {
                println("Setting language to ${parametersEvent.value}")

                _state.update {
                    it.copy(
                        languageParameter = it.languageParameter.copy(
                            value = parametersEvent.value
                        )
                    )
                }

                viewModelScope.launch {
                    parameterDao.upsert(
                        Parameter(
                            parameterId = LANGUAGE_ID,
                            value = parametersEvent.value
                        )
                    )
                }
            }
        }
        return true
    }
}
