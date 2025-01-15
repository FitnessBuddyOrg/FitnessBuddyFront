package com.project.fitnessbuddy.screens.common

sealed interface ParametersEvent {
    data object InitializeParameters : ParametersEvent

    data class SetLanguageParameterValue(val value: String) : ParametersEvent
}
