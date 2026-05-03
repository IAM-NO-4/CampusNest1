package com.campusnest1.groupq.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusnest1.groupq.ui.EventDetailsScreen
import com.campusnest1.groupq.ui.EventsScreen
import com.campusnest1.groupq.ui.registerScreen
import com.campusnest1.groupq.viewmodel.EventViewModel
//import com.campusnest1.groupq.ui.LoginScreen
import com.campusnest1.groupq.viewmodel.auth.loginViewModel
import com.example.campusnet.ui.LoginScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val loginVM: loginViewModel = viewModel()
    val eventsviewModel: EventViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { email, password ->
                    loginVM.login(email, password) { user ->
                        if (user != null) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                },
                onSignUp = { navController.navigate(Screen.Register.route) },
                onForgotPassword = {},
                onGoogleSignIn = {},
                onAppleSignIn = {}
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
            EventDetailsScreen(
                eventId = eventId, 
                viewModel = eventsviewModel,
                //onBackClick = { navController.popBackStack() }
            )
        }
    }
}
