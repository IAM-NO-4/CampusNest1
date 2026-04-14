package com.campusnest1.groupq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.campusnest1.groupq.model.*
import com.campusnest1.groupq.viewmodel.auth.registerViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        val db = Firebase.firestore
        val mod = registerViewModel()

        val user1 = User(
            userId = "wasaff",
            name = "IAM NO four",
            email = "maama7@gmail.com",
            phone = "0757716179",

        )
        val pas = "iam@12"
      mod.register(user1.email,pas,user1)








    }
}
