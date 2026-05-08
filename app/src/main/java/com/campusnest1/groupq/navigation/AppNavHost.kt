package com.campusnest1.groupq.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.campusnest1.groupq.ui.CampusNestApp
import com.campusnest1.groupq.ui.EventDetailsScreen
import com.campusnest1.groupq.ui.EventsScreen
import com.campusnest1.groupq.ui.HostelDetailsScreen
import com.campusnest1.groupq.ui.HostelSearchScreen
import com.campusnest1.groupq.ui.LoginScreen
import com.campusnest1.groupq.ui.profile.ProfileScreen
import com.campusnest1.groupq.ui.profile.PersonalInfoScreen
import com.campusnest1.groupq.ui.profile.ProfileSettingsScreen
import com.campusnest1.groupq.ui.profile.NotificationSettingsScreen
import com.campusnest1.groupq.ui.profile.BookingHistoryScreen
import com.campusnest1.groupq.ui.profile.SavedHostelsScreen
import com.campusnest1.groupq.ui.registerScreen
import com.campusnest1.groupq.viewmodel.EventViewModel
import com.campusnest1.groupq.viewmodel.AuthViewModel
import com.campusnest1.groupq.viewmodel.auth.profileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //Which screens should show the bottom bar
    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Hostels.route,
        Screen.Profile.route
    )

    val authViewModel: AuthViewModel = koinViewModel()
    val eventsviewModel: EventViewModel = koinViewModel()
    val profileViewM: profileViewModel = koinViewModel()

    val user by authViewModel.user

    // Preserving the auto-login logic
    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                BottomNavBar(navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(padding)
        ) {

            //Auth
            composable(Screen.Login.route) {
                LoginScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    onSignUp = { navController.navigate(Screen.Register.route) },

                )

            }
            
            composable(Screen.Register.route) {
                registerScreen(navController = navController)
            }


            composable (Screen.PersonalInfo.route){
                PersonalInfoScreen(navController = navController, profileView = profileViewM)
            }
            //Main Tabs
            composable(Screen.Home.route) {
                CampusNestApp(navController)
            }

            composable(Screen.Hostels.route) {
                HostelSearchScreen(navController)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(navController)
            }
                composable (Screen.ProfileSettings.route){
                    ProfileSettingsScreen(navController= navController,profileView= profileViewM)
                }

            //Top Tabs
            composable(Screen.Events.route) {
                EventsScreen(
                    navController = navController,
                    viewModel = eventsviewModel
                )
            }

            //Hidden NavBar
            composable(Screen.HostelDetails.route) { navBackStackEntry ->
                val hostelId = navBackStackEntry.arguments?.getString("hostelId") ?: ""
                HostelDetailsScreen(
                    hostelId = hostelId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.EventDetails.route) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                val event = eventsviewModel.events.value.find { it.eventId == eventId }

                if (event != null) {
                    EventDetailsScreen(
                        event = event,
                        viewModel = eventsviewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                } else {
                    Text("Event details not available")
                }
            }

            composable(Screen.NotificationSettings.route) {
                NotificationSettingsScreen(navController = navController)
            }

            composable(Screen.BookingHistory.route) {
                BookingHistoryScreen(navController = navController)
            }

            composable(Screen.SavedHostels.route) {
                SavedHostelsScreen(navController = navController)
            }
            
            composable("notifications") {
                Text("Notifications Screen")
            }
        }
    }
}
