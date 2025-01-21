package com.project.fitnessbuddy.api.auth

import RetrofitInstance
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.project.fitnessbuddy.MainActivity
import com.project.fitnessbuddy.database.dao.ExerciseDao
import com.project.fitnessbuddy.database.dao.UserDao
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.User
import com.project.fitnessbuddy.database.entity.enums.Category
import com.project.fitnessbuddy.database.entity.enums.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class AuthViewModel(
    application: Application,
    private val userDao: UserDao,
    private val exerciseDao: ExerciseDao
) : AndroidViewModel(application) {

    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val appContext: Context = application.applicationContext

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = RetrofitInstance.authService.login(LoginRequest(email, password))
                _userState.value = UserState(
                    user = User(
                        accessToken = response.accessToken,
                        email = email,
                        userId = response.id
                    ),
                    isLoggedIn = true
                )
                _error.value = null
                saveToken(response.accessToken, email, response.id)
                onEvent(AuthEvent.UpsertUser)
            } catch (e: Exception) {
                _error.value = "Login failed: ${e.localizedMessage}"
                Toast.makeText(appContext, "Login failed: ${e.localizedMessage}", Toast.LENGTH_LONG)
                    .show()
            } finally {
                _loading.value = false
            }
        }
    }


    //TODO handle errors
    fun register(email: String, password: String, confirmPassword: String) {
        _loading.value = true

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authService.register(
                    RegisterRequest(
                        email,
                        password,
                        confirmPassword
                    )
                )
                _userState.value = UserState(
                    user = User(
                        accessToken = response.accessToken,
                        email = email,
                        userId = response.id
                    ),
                    isLoggedIn = true
                )
                Toast.makeText(appContext, "Registration successful", Toast.LENGTH_LONG).show()
                onEvent(AuthEvent.UpsertUser)
            } catch (e: Exception) {
                _error.value = "Registration failed: ${e.localizedMessage}"
                Toast.makeText(
                    appContext,
                    "Registration failed: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                _loading.value = false
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
                val response = credentialManager.getCredential(activity, getRequest)
                Log.d(
                    "AuthViewModel",
                    "Google Sign-In successful: ${bundleToJson(response.credential.data)}"
                )
                handleGoogleLoginSuccess(response)
            } catch (e: GetCredentialException) {
                _error.value = "Google Sign-In failed: ${e.localizedMessage}"
            } catch (e: Exception) {
                Log.e("AuthViewModel", "An unexpected error occurred: ${e.localizedMessage}", e)
                Toast.makeText(
                    appContext,
                    "An unexpected error occurred: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleGoogleLoginSuccess(response: GetCredentialResponse) {
        val credential = response.credential
        if (credential is CustomCredential) {
            val idToken =
                credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN")
            val profilePictureUrl =
                credential.data.getParcelable<Uri>("com.google.android.libraries.identity.googleid.BUNDLE_KEY_PROFILE_PICTURE_URI")
                    ?.toString()
            if (idToken != null && profilePictureUrl != null) {
                viewModelScope.launch {
                    try {
                        _loading.value = true
                        val userResponse = RetrofitInstance.authService.googleLogin(
                            GoogleLoginRequest(idToken, profilePictureUrl)
                        )
                        _userState.value = UserState(
                            user = User(
                                name = credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_DISPLAY_NAME"),
                                accessToken = userResponse.accessToken,
                                email = userResponse.email,
                                userId = userResponse.id,
                            ),
                            isLoggedIn = true
                        )
                        saveToken(userResponse.accessToken, userResponse.email, userResponse.id)
                        onEvent(AuthEvent.UpsertUser)
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Google Sign-In failed: ${e.localizedMessage}", e)
                    } finally {
                        _loading.value = false
                    }
                }
            } else {
                _error.value = "Google Sign-In failed: ID Token is null"
            }
        } else {
            _error.value = "Google Sign-In failed: Credential is not a GoogleIdTokenCredential"
        }
    }

    fun loginWithGitHub(activity: Activity) {
        val clientId = "Ov23liGkKX58yCWWxwHo"
        val redirectUri = "https://fitbud.ldelatullaye.fr/login/oauth2/code/github"
        val githubLoginUrl =
            "https://github.com/login/oauth/authorize?client_id=$clientId&redirect_uri=$redirectUri&scope=user:email"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubLoginUrl))
        activity.startActivity(intent)
    }

    fun handleSuccessfulLogin(accessToken: String, email: String, id: Long) {
        _userState.value = UserState(
            user = User(
                accessToken = accessToken,
                email = email,
                userId = id
            ),
            isLoggedIn = true
        )
        saveToken(accessToken, email, id)
        onEvent(AuthEvent.UpsertUser)
    }

    private fun saveToken(token: String, email: String, id: Long) {
        val sharedPreferences: SharedPreferences =
            appContext.getSharedPreferences("FitnessBuddyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", token)
        editor.putString("email", email)
        editor.putLong("id", id)
        editor.apply()
    }

    fun loadToken() {
        val sharedPreferences: SharedPreferences =
            appContext.getSharedPreferences("FitnessBuddyPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("accessToken", null)
        val email = sharedPreferences.getString("email", null)
        val id = sharedPreferences.getLong("id", 0)
        if (token != null && email != null) {
            _userState.value = UserState(
                user = User(
                    accessToken = token,
                    email = email,
                    userId = id
                ),
                isLoggedIn = true
            )
            onEvent(AuthEvent.UpsertUser)
        }
    }

    fun clearToken() {
        val sharedPreferences: SharedPreferences =
            appContext.getSharedPreferences("FitnessBuddyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        _userState.value = UserState()
    }

    private fun bundleToJson(bundle: Bundle): String {
        val json = JSONObject()
        for (key in bundle.keySet()) {
            json.put(key, bundle.get(key))
        }
        return json.toString()
    }

    fun onEvent(authEvent: AuthEvent) {
        when (authEvent) {
            is AuthEvent.UpsertUser -> {
                viewModelScope.launch {
                    var userId = userDao.upsert(_userState.value.user)

                    if (userId == -1L) {
                        userId = _userState.value.user.userId ?: -1
                    }
                    val userExercises = exerciseDao.getExercises(userId)

                    if (userExercises.isEmpty()) {
                        RetrofitInstance.exerciseApi.getTemplateExercise()
                            .forEach { templateExerciseDTO ->
                                val exercise = Exercise(
                                    name = templateExerciseDTO.name ?: "",
                                    instructions = templateExerciseDTO.instructions ?: "",
                                    videoLink = templateExerciseDTO.videoLink ?: "",
                                    category = templateExerciseDTO.category ?: Category.CHEST,
                                    language = templateExerciseDTO.language ?: Language.CUSTOM,
                                    userId = userId
                                )
                                println("Upserting exercise: $exercise")

                                exerciseDao.upsert(exercise)
                            }
                    }
                }
            }
        }
    }

    fun updateProfilePictureUrl(url: String) {
        _userState.value = _userState.value.copy(profilePictureUrl = url)
    }

    private fun getRolesFromToken(token: String): List<String> {
        val jwt = token.split(".")[1]
        val decoded = String(android.util.Base64.decode(jwt, android.util.Base64.DEFAULT))
        val json = JSONObject(decoded)
        val roles = json.getJSONArray("roles")
        val rolesList = mutableListOf<String>()
        for (i in 0 until roles.length()) {
            rolesList.add(roles.getString(i))
        }
        Log.d("AuthViewModel", "Roles: $rolesList")
        return rolesList
    }

    fun hasRole(userState: UserState, role: String): Boolean {
        return getRolesFromToken(userState.user.accessToken.orEmpty()).contains(role)
    }
}


