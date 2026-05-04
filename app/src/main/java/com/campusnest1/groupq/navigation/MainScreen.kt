package com.campusnest1.groupq.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.campusnest1.groupq.ui.CampusNestApp
import com.campusnest1.groupq.ui.HostelSearchScreen
import com.campusnest1.groupq.ui.profile.ProfileScreen
import androidx.compose.foundation.layout.padding
import com.campusnest1.groupq.ui.NotificationsSheet
import com.campusnest1.groupq.ui.HostelDetailsScreen

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
                CampusNestApp(navController)
            }

            composable("hostels") {
                HostelSearchScreen(navController)
            }

            composable("profile") {
                ProfileScreen(navController)
            }
            composable("notifications") {
                NotificationsSheet(navController)
            }
            //for the view deatils button
            composable(
                route = "hostel_details/{hostelId}",
                arguments = listOf(navArgument("hostelId") { type = NavType.StringType })
               )   { backStackEntry ->
                val hostelId = backStackEntry.arguments?.getString("hostelId")

                val hostel = MockData.mockHostels.find { it.hostelId == hostelId }

                if (hostel != null) {
                    HostelDetailsScreen(
                        hostel = hostel,
                        rooms = MockData.mockRooms,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}