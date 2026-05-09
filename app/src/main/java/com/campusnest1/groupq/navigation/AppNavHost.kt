package com.campusnest1.groupq.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    var isBottomBarVisible by remember { mutableStateOf(true) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Which screens should show the floating bottom bar
    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Hostels.route,
        Screen.Profile.route,
        Screen.Events.route
    )

    val authViewModel: AuthViewModel = koinViewModel()
    val eventsviewModel: EventViewModel = koinViewModel()
    val profileViewM: profileViewModel = koinViewModel()

    val user by authViewModel.user

    // Navigate to Home if user is logged in and on the login screen
    LaunchedEffect(user, currentRoute) {
        if (user != null && currentRoute == Screen.Login.route) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.fillMaxSize()
        ) {
            // Auth
            composable(Screen.Login.route) {
                LoginScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    onSignUp = { navController.navigate(Screen.Register.route) }
                )
            }
            
            composable(Screen.Register.route) {
                registerScreen(navController = navController)
            }

            composable(Screen.PersonalInfo.route) {
                PersonalInfoScreen(navController = navController, profileView = profileViewM)
            }

            // Main Tabs
            composable(Screen.Home.route) {
                CampusNestApp(navController, onScroll = { visible -> isBottomBarVisible = visible })
            }

            composable(Screen.Hostels.route) {
                HostelSearchScreen(navController, onScroll = { visible -> isBottomBarVisible = visible })
            }

            composable(Screen.Profile.route) {
                ProfileScreen(navController, onScroll = { visible -> isBottomBarVisible = visible })
            }

            composable(Screen.ProfileSettings.route) {
                ProfileSettingsScreen(navController = navController, profileView = profileViewM)
            }

            composable(Screen.Events.route) {
                EventsScreen(navController = navController, viewModel = eventsviewModel, onScroll = { visible -> isBottomBarVisible = visible })
            }

            // Details (No Bottom Bar)
            composable(
                route = Screen.HostelDetails.route,
                arguments = listOf(navArgument("hostelId") { type = NavType.StringType })
            ) { backStackEntry ->
                val hostelId = backStackEntry.arguments?.getString("hostelId") ?: ""
                HostelDetailsScreen(
                    hostelId = hostelId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EventDetails.route,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                val event = eventsviewModel.events.value.find { it.eventId == eventId }

                if (event != null) {
                    EventDetailsScreen(
                        event = event,
                        viewModel = eventsviewModel,
                        onBackClick = { navController.popBackStack() }
                    )
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

        // Floating Bottom Nav Bar
        if (currentRoute in bottomBarScreens) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding() // Avoid system nav bar
                    .padding(bottom = 16.dp)
            ) {
                BottomNavBar(
                    navController,
                    isVisible = isBottomBarVisible

                )
            }
        }
    }
}
