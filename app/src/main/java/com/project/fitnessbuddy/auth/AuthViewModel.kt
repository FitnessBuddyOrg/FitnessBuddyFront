package com.project.fitnessbuddy.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
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

    fun loginWithGoogle(activity: Activity) {
        val googleIdOption = GetGoogleIdOption.Builder()
            //.setServerClientId("663662917989-ckm453b7gt3dfmp29c74evda0nh2ki46.apps.googleusercontent.com") //android
            .setServerClientId("663662917989-055c6as89abel9k2tb3fvri5kkj552r6.apps.googleusercontent.com") //web
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val getRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(activity.applicationContext)

        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Starting Google Sign-In")
                val response = credentialManager.getCredential(activity, getRequest)
                Log.d("AuthViewModel", "Google Sign-In response received: $response")
                handleGoogleLoginSuccess(response)
            } catch (e: GetCredentialException) {
                Log.e("AuthViewModel", "Google Sign-In failed: ${e.localizedMessage}", e)
                _error.value = "Google Sign-In failed: ${e.localizedMessage}"
                Toast.makeText(activity, "Google Sign-In failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "An unexpected error occurred: ${e.localizedMessage}", e)
                _error.value = "An unexpected error occurred: ${e.localizedMessage}"
                Toast.makeText(activity, "An unexpected error occurred: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
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
                        Log.d("AuthViewModel", "Google Sign-In successful with my metyhod, ID Token: $idToken")
                        val userResponse = RetrofitInstance.authService.googleLogin(GoogleLoginRequest(idToken))
                        Log.d("AuthViewModel", "UserResponse from googleLogin: $userResponse")
                        _userState.value = UserState(
                            accessToken = userResponse.accessToken,
                            isLoggedIn = true,
                            name = credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_DISPLAY_NAME"),
                            email = userResponse.email
                        )
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
    }
}

