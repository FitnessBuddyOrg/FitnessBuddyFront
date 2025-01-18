package com.project.fitnessbuddy.api.auth

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val appContext: Context = application.applicationContext

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authService.login(LoginRequest(email, password))
                _userState.value = UserState(
                    accessToken = response.accessToken,
                    isLoggedIn = true,
                    email = email
                )
                _error.value = null
                saveToken(response.accessToken, email)
            } catch (e: Exception) {
                _error.value = "Login failed: ${e.localizedMessage}"
                Toast.makeText(appContext, "Login failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }


    //TODO handle errors
    fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authService.register(RegisterRequest(email, password, confirmPassword))
                _userState.value = UserState(
                    accessToken = response.accessToken,
                    isLoggedIn = true,
                    email = email
                )
                Toast.makeText(appContext, "Registration successful", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                _error.value = "Registration failed: ${e.localizedMessage}"
                Toast.makeText(appContext, "Registration failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun logout() {
        clearToken()
    }

    fun loginWithGoogle(activity: Activity) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("663662917989-055c6as89abel9k2tb3fvri5kkj552r6.apps.googleusercontent.com")
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val getRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(appContext)

        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Starting Google Sign-In")
                val response = credentialManager.getCredential(activity, getRequest)
                Log.d("AuthViewModel", "Google Sign-In response received: $response")
                handleGoogleLoginSuccess(response)
            } catch (e: GetCredentialException) {
                Log.e("AuthViewModel", "Google Sign-In failed: ${e.localizedMessage}", e)
                _error.value = "Google Sign-In failed: ${e.localizedMessage}"
                Toast.makeText(appContext, "Google Sign-In failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "An unexpected error occurred: ${e.localizedMessage}", e)
                _error.value = "An unexpected error occurred: ${e.localizedMessage}"
                Toast.makeText(appContext, "An unexpected error occurred: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleGoogleLoginSuccess(response: GetCredentialResponse) {
        Log.d("AuthViewModel", "Processing Google Sign-In response: $response")
        val credential = response.credential
        if (credential is CustomCredential) {
            Log.d("AuthViewModel", "CustomCredential received: ${credential.data}")
            val idToken = credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN")
            if (idToken != null) {
                viewModelScope.launch {
                    try {
                        Log.d("AuthViewModel", "Google Sign-In successful, ID Token: $idToken")
                        val userResponse = RetrofitInstance.authService.googleLogin(
                            GoogleLoginRequest(idToken)
                        )
                        Log.d("AuthViewModel", "UserResponse from googleLogin: $userResponse")
                        _userState.value = UserState(
                            accessToken = userResponse.accessToken,
                            isLoggedIn = true,
                            name = credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_DISPLAY_NAME"),
                            email = userResponse.email
                        )
                        saveToken(userResponse.accessToken, userResponse.email)
                        Log.d("AuthViewModel", "Google Sign-In successful, UserResponse: $userResponse")
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Google Sign-In failed: ${e.localizedMessage}", e)
                        _error.value = "Google Sign-In failed: ${e.localizedMessage}"
                    }
                }
            } else {
                Log.e("AuthViewModel", "Google Sign-In failed: ID Token is null")
                _error.value = "Google Sign-In failed: ID Token is null"
            }
        } else {
            Log.e("AuthViewModel", "Google Sign-In failed: Credential is not a GoogleIdTokenCredential, actual type: ${credential?.javaClass?.name}")
            _error.value = "Google Sign-In failed: Credential is not a GoogleIdTokenCredential"
        }
    }

    fun loginWithGitHub(activity: Activity) {
        val clientId = "Ov23liGkKX58yCWWxwHo"
        val redirectUri = "https://fitbud.ldelatullaye.fr/login/oauth2/code/github"
        val githubLoginUrl = "https://github.com/login/oauth/authorize?client_id=$clientId&redirect_uri=$redirectUri&scope=user:email"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubLoginUrl))
        activity.startActivity(intent)
    }

    fun handleSuccessfulLogin(accessToken: String, email: String) {
        _userState.value = UserState(
            accessToken = accessToken,
            isLoggedIn = true,
            email = email
        )
        saveToken(accessToken, email)
    }

    private fun saveToken(token: String, email: String) {
        val sharedPreferences: SharedPreferences = appContext.getSharedPreferences("FitnessBuddyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", token)
        editor.putString("email", email)
        editor.apply()
    }
    fun loadToken() {
        val sharedPreferences: SharedPreferences = appContext.getSharedPreferences("FitnessBuddyPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("accessToken", null)
        val email = sharedPreferences.getString("email", null)

        if (token != null && email != null) {
            _userState.value = UserState(
                accessToken = token,
                isLoggedIn = true,
                email = email
            )
        }
    }

    fun clearToken() {
        val sharedPreferences: SharedPreferences = appContext.getSharedPreferences("FitnessBuddyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        _userState.value = UserState()
    }
}


