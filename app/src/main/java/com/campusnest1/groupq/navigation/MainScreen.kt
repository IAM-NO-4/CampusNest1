package com.campusnest1.groupq.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.campusnest1.groupq.navigation.Screen
import com.campusnest1.groupq.ui.CampusNestApp
import com.campusnest1.groupq.ui.EventsScreen
import com.campusnest1.groupq.ui.HostelDetailsScreen
import com.campusnest1.groupq.ui.HostelSearchScreen
import com.campusnest1.groupq.ui.NotificationsSheet
import com.campusnest1.groupq.ui.profile.PersonalInfoScreen
import com.campusnest1.groupq.ui.profile.ProfileScreen
import com.campusnest1.groupq.ui.profile.ProfileSettingsScreen
import com.campusnest1.groupq.viewmodel.EventViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.padding

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
            startDestination = "home",
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

            composable("events") {
                val eventViewModel: EventViewModel = koinViewModel()
                EventsScreen(navController = navController, viewModel = eventViewModel)
            }

            composable(Screen.PersonalInfo.route) {
                PersonalInfoScreen(navController)
            }

            composable(Screen.ProfileSettings.route) {
                ProfileSettingsScreen(navController)
            }

            composable("notifications") {
                NotificationsSheet(
                    navController = navController,
                    notifications = emptyList(),
                    onDismiss = { navController.popBackStack() },
                    onDelete = {},
                    onNotificationClick = {}
                )
            }
            //for the view deatils button
            composable(
                route = "hostel_details/{hostelId}",
                arguments = listOf(navArgument("hostelId") { type = NavType.StringType })
               )   { backStackEntry ->
                val hostelId = backStackEntry.arguments?.getString("hostelId") ?: return@composable
                HostelDetailsScreen(
                    hostelId = hostelId,
                    onBackClick = { navController.popBackStack() }
                )
            }

        }
    }
}