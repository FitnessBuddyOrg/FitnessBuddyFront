package com.project.fitnessbuddy.api.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class ProfileViewModel(private val userApi: UserApi) : ViewModel() {

    private val _user = MutableStateFlow<UserDTO?>(null)
    val user: StateFlow<UserDTO?> = _user
    private val _profilePictureUrl = MutableStateFlow<String?>(null)
    val profilePictureUrl: StateFlow<String?> = _profilePictureUrl

    fun fetchUser() {
        viewModelScope.launch {
            try {
                val user = userApi.getMe()
                _user.value = user
                _profilePictureUrl.value = userApi.getProfilePicture().url
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

    fun updateProfilePicture(file: File) {
        viewModelScope.launch {
            try {
                val requestFile = MultipartBody.Part.createFormData(
                    "file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
                )
                val response = userApi.updateProfilePicture(requestFile)
                _profilePictureUrl.value = response.url
            } catch (e: Exception) {
                println("Error updating profile picture: ${e.message}")
            }
        }
    }

    fun clearUserData() {
        _user.value = null
    }
}
