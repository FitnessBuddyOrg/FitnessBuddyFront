package com.project.fitnessbuddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.project.fitnessbuddy.auth.AuthViewModel
import com.project.fitnessbuddy.auth.GitHubTokenRequestDTO
import com.project.fitnessbuddy.auth.RetrofitInstance
import com.project.fitnessbuddy.database.FitnessBuddyDatabase
import com.project.fitnessbuddy.navigation.AppNavGraph
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.exercises.ExercisesViewModel
import com.project.fitnessbuddy.ui.theme.FitnessBuddyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel>()
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            FitnessBuddyDatabase::class.java,
            "fitnessBuddy.db"
        ).build()
    }

    override fun onStart() {
        super.onStart()
        handleRedirect(intent)
    }

    private fun handleRedirect(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null) {
            when {
                data.toString().startsWith("https://fitbud.ldelatullaye.fr/login/oauth2/code/github") -> {
                    val code = data.getQueryParameter("code")
                    if (code != null) {
                        Log.d("GitHubAuth", "Intent data: $data")

                        exchangeCodeForToken(code)
                    } else {
                        val error = data.getQueryParameter("error")
                        Log.e("GitHubAuth", "GitHub login failed: $error")
                    }
                }

                // Handle custom scheme redirection (fitnessbuddy://auth)
                data.toString().startsWith("fitnessbuddy://auth") -> {
                    val token = data.getQueryParameter("token")
                    val email = data.getQueryParameter("email")
                    if (token != null && email != null) {
                        authViewModel.handleSuccessfulLogin(token, email)
                        Log.d("GitHubAuth", "Token received: $token, Email: $email")
                    } else {
                        Log.e("GitHubAuth", "Missing token or email in custom scheme redirect.")
                    }
                }
            }
        }
    }


    private fun exchangeCodeForToken(code: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.authService.githubLogin(GitHubTokenRequestDTO(code))
                authViewModel.handleSuccessfulLogin(response.accessToken, response.email)
            } catch (e: Exception) {
                Log.e("GitHubAuth", "Failed to exchange code for token: ${e.localizedMessage}", e)
            }
        }
    }


//    private val navigationViewModel: NavigationViewModel by lazy {
//        ViewModelProvider(this)[NavigationViewModel::class.java]
//    }

    private val exercisesViewModel by viewModels<ExercisesViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExercisesViewModel(db.exerciseDao) as T
                }
            }
        }
    )

    private val navigationViewModel by viewModels<NavigationViewModel> (
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NavigationViewModel() as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessBuddyTheme {
                val navigationState by navigationViewModel.state.collectAsState()
                val exerciseState by exercisesViewModel.state.collectAsState()
                val userState by authViewModel.userState.collectAsState()
                AppNavGraph(
                    navigationState = navigationState,
                    navigationViewModel = navigationViewModel,
                    exercisesState = exerciseState,
                    userState = userState,
                    authViewModel = authViewModel,
                    exercisesViewModel = exercisesViewModel
                )
            }
        }
    }

}


