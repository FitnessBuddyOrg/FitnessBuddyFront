package com.project.fitnessbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.project.fitnessbuddy.app.AppNavGraph
import com.project.fitnessbuddy.ui.theme.FitnessBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessBuddyTheme {
//                drawerActivity()
                AppNavGraph()
            }
        }
    }
}


