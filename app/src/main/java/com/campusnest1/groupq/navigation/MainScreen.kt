package com.campusnest1.groupq.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.campusnest1.groupq.ui.HomeScreen
import com.campusnest1.groupq.ui.HostelScreen
import com.campusnest1.groupq.ui.profile.profileScreen

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "hostels",
            modifier = Modifier.padding(padding)
        ) {

            composable("home") {
                HomeScreen(navController)
            }

           composable("hostels") {
                HostelScreen(navController)
            }

           composable("profile") {
                ProfileScreen(navController)
            }
        }
    }
}