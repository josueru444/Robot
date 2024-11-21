package com.example.robotjardinero.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.robotjardinero.Routes
import com.example.robotjardinero.db.RealtimeManager
import com.example.robotjardinero.ui.forms.AddPlant
import com.example.robotjardinero.ui.home.HomePage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val context = LocalContext.current

    val realTimeManager = RealtimeManager(context)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Robot Jardinero") },
                navigationIcon = {
                    if (currentRoute != Routes.homePage) {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Menu lateral")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentRoute == Routes.homePage) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.addPlantPage)
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "")
                }
            }
        },

        ) { innerPadding ->
        NavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = Routes.homePage
        ) {
            composable(Routes.homePage) {
                HomePage(navController = navController, realTimeManager = realTimeManager)
            }
            composable(Routes.addPlantPage) {
                AddPlant(navigation = navController, realTimeManager = realTimeManager)
            }

        }
    }
}