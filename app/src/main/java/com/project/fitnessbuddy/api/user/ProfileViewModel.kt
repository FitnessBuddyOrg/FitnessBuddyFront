package com.project.fitnessbuddy.api.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userApi: UserApi) : ViewModel() {

    private val _user = MutableStateFlow<UserDTO?>(null)
    val user: StateFlow<UserDTO?> = _user

    fun fetchUser() {
        viewModelScope.launch {
            try {
                _user.value = userApi.getMe()
            } catch (e: Exception) {
                println("Error fetching user: ${e.message}")
            }
        }
    }

    fun updateUser(id: Long, name: String) {
        viewModelScope.launch {
            try {
                val updatedUser = userApi.updateMe(UpdateUser(id, name))
                _user.value = updatedUser
            } catch (e: Exception) {
                println("Error updating user: ${e.message}")
            }
        }
    }

    fun clearUserData() {
        _user.value = null
    }
}
