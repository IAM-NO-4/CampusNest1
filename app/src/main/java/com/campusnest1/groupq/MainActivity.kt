package com.campusnest1.groupq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.campusnest1.groupq.ui.theme.CampusNestTheme
import com.campusnest1.groupq.model.*
import com.campusnest1.groupq.viewmodel.auth.registerViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import com.campusnest1.groupq.navigation.AppNavHost
import com.campusnest1.groupq.ui.CampusNestApp
import com.campusnest1.groupq.navigation.MainScreen

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

//        val db = Firebase.firestore
//        val mod = registerViewModel()
//
//        val pas = "iam@12"

        setContent {
            CampusNestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }

    }
}
