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
import com.campusnest1.groupq.navigation.AppNavHost
import com.campusnest1.groupq.navigation.Screen
import com.campusnest1.groupq.ui.registerScreen
import com.campusnest1.groupq.ui.theme.CampusNestTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : ComponentActivity() {
    // The AppNavHost uses its own injected viewModel, 
    // but we can keep this if needed for other logic.
    // However, AppNavHost is now the entry point.
    // private val viewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusNestTheme {
                AppNavHost()
            }
        }
    }
}