package com.campusnest1.groupq.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campusnet.ui.LoginScreen
import com.campusnest1.groupq.viewmodel.auth.loginViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val loginVM = loginViewModel()

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
            HomeScreen(navController)
        }

        composable(Screen.Details.route) {
            Text(text = "Details Screen")
        }
    }
}

@Composable
fun HomeScreen(x0: NavHostController) {
  //
}
