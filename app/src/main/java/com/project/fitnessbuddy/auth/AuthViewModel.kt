package com.project.fitnessbuddy.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(context: Context, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authService.login(LoginRequest(email, password))
                _userState.value = UserState(
                    accessToken = response.accessToken,
                    isLoggedIn = true,
                    email = email
                )
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Login failed: ${e.localizedMessage}"
                Toast.makeText(context, "Login failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun register(context: Context, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authService.register(RegisterRequest(email, password, confirmPassword))
                _userState.value = UserState(
                    accessToken = response.accessToken,
                    isLoggedIn = true,
                    email = email
                )
                Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                _error.value = "Registration failed: ${e.localizedMessage}"
                Toast.makeText(context, "Registration failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun logout() {
        _userState.value = UserState()
    }
}