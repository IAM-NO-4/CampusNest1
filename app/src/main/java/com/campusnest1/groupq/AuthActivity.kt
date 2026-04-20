package com.campusnest1.groupq

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.campusnet.ui.LoginScreen
import com.campusnest1.groupq.ui.theme.CampusNestTheme
import com.campusnest1.groupq.viewmodel.auth.loginViewModel

class AuthActivity : ComponentActivity() {
    private val viewModel: loginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusNestTheme {
                LoginScreen(
                    onLoginClick = { email, password ->
                        viewModel.login(email, password) { user ->
                            if (user != null) {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                        }
                    },
                    onSignUp = {},
                    onForgotPassword = {},
                    onGoogleSignIn = {},
                    onAppleSignIn = {}
                )
            }
        }
    }
}