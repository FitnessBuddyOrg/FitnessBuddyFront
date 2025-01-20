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
import com.project.fitnessbuddy.api.auth.AuthViewModel
import com.project.fitnessbuddy.api.auth.GitHubTokenRequestDTO
import com.project.fitnessbuddy.screens.statistics.StatisticsViewModel
import com.project.fitnessbuddy.database.FitnessBuddyDatabase
import com.project.fitnessbuddy.navigation.AppNavGraph
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.ParametersViewModel
import com.project.fitnessbuddy.screens.exercises.ExercisesViewModel
import com.project.fitnessbuddy.screens.routines.RoutinesViewModel
import com.project.fitnessbuddy.ui.theme.FitnessBuddyTheme
import kotlinx.coroutines.launch
import com.github.mikephil.charting.utils.Utils
import com.project.fitnessbuddy.api.user.ProfileViewModel


class MainActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(application) as T
            }
        }
    }

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            FitnessBuddyDatabase::class.java,
            FitnessBuddyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
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
                    val id = data.getQueryParameter("id")
                    if (token != null && email != null && id != null) {
                        authViewModel.handleSuccessfulLogin(token, email, id.toLong())
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
                authViewModel.handleSuccessfulLogin(response.accessToken, response.email, response.id)
            } catch (e: Exception) {
                Log.e("GitHubAuth", "Failed to exchange code for token: ${e.localizedMessage}", e)
            }
        }
    }

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

    private val routinesViewModel by viewModels<RoutinesViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RoutinesViewModel(
                        routineDao = db.routineDao,
                        routineExerciseDao = db.routineExerciseDao,
                        routineExerciseSetDao = db.routineExerciseSetDao,
                    ) as T
                }
            }
        }
    )

    private val parametersViewModel by viewModels<ParametersViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ParametersViewModel(db.parameterDao) as T
                }
            }
        }
    )

    private val statisticsViewModel by viewModels<StatisticsViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return StatisticsViewModel(RetrofitInstance.userApi, db.routineDao) as T
            }
        }
    }

    private val profileViewModel by viewModels<ProfileViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(RetrofitInstance.userApi) as T
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.init(this)
        authViewModel.loadToken()
        RetrofitInstance.initialize {
            authViewModel.userState.value.accessToken
        }
        setContent {
            FitnessBuddyTheme {
                val navigationState by navigationViewModel.state.collectAsState()
                val exerciseState by exercisesViewModel.state.collectAsState()
                val routinesState by routinesViewModel.state.collectAsState()
                val statisticsState by statisticsViewModel.state.collectAsState()

                val userState by authViewModel.userState.collectAsState()
                val parametersState by parametersViewModel.state.collectAsState()

                AppNavGraph(
                    navigationState = navigationState,
                    navigationViewModel = navigationViewModel,

                    exercisesState = exerciseState,
                    exercisesViewModel = exercisesViewModel,

                    routinesState = routinesState,
                    routinesViewModel = routinesViewModel,

                    parametersState = parametersState,
                    parametersViewModel = parametersViewModel,

                    userState = userState,
                    authViewModel = authViewModel,

                    statisticsState = statisticsState,
                    statisticsViewModel = statisticsViewModel,

                    profileViewModel = profileViewModel
                )
            }
        }
        if (authViewModel.userState.value.isLoggedIn) {
            lifecycleScope.launch {
                try {
                    RetrofitInstance.userApi.incrementAppOpen()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Failed to increment app open count: ${e.localizedMessage}", e)
                }
            }
        }
    }
}


