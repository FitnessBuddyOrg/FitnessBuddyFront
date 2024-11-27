package com.project.fitnessbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.fitnessbuddy.ui.theme.fitnessBuddyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            fitnessBuddyTheme {
                drawerActivity()
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun drawerActivity() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            drawerContent()
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("Empty Drawer Example") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            content = { innerPadding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Main Content",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        )
    }
}

@Composable
fun drawerContent() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.7f

    Box(
        modifier = Modifier
            .width(drawerWidth)
            .fillMaxHeight()
            .background(Color.White)
    ) {
        Text(
            text = "Drawer Content",
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            color = Color.Black
        )
    }
    }

@Preview(showBackground = true)
@Composable
fun drawerPreview() {
    drawerActivity()
}
