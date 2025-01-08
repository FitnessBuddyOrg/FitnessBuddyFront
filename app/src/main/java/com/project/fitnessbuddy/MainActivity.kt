package com.project.fitnessbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.project.fitnessbuddy.database.FitnessBuddyDatabase
import com.project.fitnessbuddy.navigation.AppNavGraph
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.ParametersEvent
import com.project.fitnessbuddy.screens.common.ParametersViewModel
import com.project.fitnessbuddy.screens.exercises.ExercisesViewModel
import com.project.fitnessbuddy.ui.theme.FitnessBuddyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            FitnessBuddyDatabase::class.java,
            "fitnessBuddy.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

//    private val navigationViewModel: NavigationViewModel by lazy {
//        ViewModelProvider(this)[NavigationViewModel::class.java]
//    }

    private val navigationViewModel by viewModels<NavigationViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NavigationViewModel() as T
                }
            }
        }
    )

    private val exercisesViewModel by viewModels<ExercisesViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExercisesViewModel(db.exerciseDao) as T
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessBuddyTheme {
                val navigationState by navigationViewModel.state.collectAsState()
                val exerciseState by exercisesViewModel.state.collectAsState()
                val parametersState by parametersViewModel.state.collectAsState()

                AppNavGraph(
                    navigationState = navigationState,
                    navigationViewModel = navigationViewModel,

                    exercisesState = exerciseState,
                    exercisesViewModel = exercisesViewModel,

                    parametersState = parametersState,
                    parametersViewModel = parametersViewModel
                )
            }
        }
    }
}


