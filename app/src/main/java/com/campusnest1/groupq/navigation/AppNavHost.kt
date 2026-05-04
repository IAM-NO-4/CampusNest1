package com.campusnest1.groupq.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusnest1.groupq.ui.EventDetailsScreen
import com.campusnest1.groupq.ui.EventsScreen
import com.campusnest1.groupq.ui.registerScreen
import com.campusnest1.groupq.viewmodel.EventViewModel
import com.campusnest1.groupq.viewmodel.AuthViewModel
import com.example.campusnet.ui.LoginScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // Use koinViewModel to get the injected instances
    val authViewModel: AuthViewModel = koinViewModel()
    val eventsviewModel: EventViewModel = koinViewModel()

    val user by authViewModel.user

    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onSignUp = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Home.route) {
            MainScreen()
        }
        
        composable(Screen.Register.route) {
           registerScreen(
               navController = navController
           )
        }
        
        composable(Screen.Events.route) {
            EventsScreen(
                navController = navController, 
                viewModel = eventsviewModel
            )
        }
        
        composable(Screen.Eventdetails.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            // Find the event in the list held by the ViewModel
            val event = eventsviewModel.events.value.find { it.eventId == eventId }
            
            if (event != null) {
                EventDetailsScreen(
                    event = event,
                    viewModel = eventsviewModel,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                // If event is not found, you could show an error or navigate back
                Text("Event details not available")
            }
        }
    }
}
