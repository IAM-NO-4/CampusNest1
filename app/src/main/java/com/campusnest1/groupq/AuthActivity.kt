package com.campusnest1.groupq

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import com.example.campusnet.ui.LoginScreen
import com.campusnest1.groupq.viewmodel.AuthViewModel
import com.campusnest1.groupq.navigation.Screen
import com.campusnest1.groupq.ui.registerScreen
import com.campusnest1.groupq.ui.theme.CampusNestTheme

class AuthActivity : ComponentActivity() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusNestTheme {
                val navController = rememberNavController()
                
                NavHost(navController = navController, startDestination = Screen.Login.route) {
                    composable(Screen.Login.route) {
                        LoginScreen(
                            authViewModel = viewModel,
                            onLoginClick = { email: String, password: String ->
                                viewModel.login(email, password)
                            },
                            onSignUp = {
                                navController.navigate(Screen.Register.route)
                            },
                            onForgotPassword = {},
                            onGoogleSignIn = {},
                            onAppleSignIn = {}
                        )
                    }
                    composable(Screen.Register.route) {
                        registerScreen(
                            navController = navController,
                            onRegisterSuccess = {
                                startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                                finish()
                            }
                        )
                    }
                }

                // Add observer for login success
                LaunchedEffect(viewModel.user.value) {
                    if (viewModel.user.value != null) {
                        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}