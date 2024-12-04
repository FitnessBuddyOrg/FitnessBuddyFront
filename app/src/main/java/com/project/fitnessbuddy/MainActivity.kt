package com.project.fitnessbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.project.fitnessbuddy.ui.theme.fitnessBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            fitnessBuddyTheme {
//                drawerActivity()
                sampleAppNavGraph()
            }
        }
    }
}


