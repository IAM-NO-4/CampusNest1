package com.campusnest1.groupq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.campusnest1.groupq.entities.*
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import com.campusnest1.groupq.ui.CampusNestApp
import com.campusnest1.groupq.ui.theme.CampusNestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        val db = Firebase.firestore

        for (i in 0..10) {
            val hostel = Hostel(
                hostelId = "hostel$i",
                name = "Hotel #$i",
                location = if (i < 3) {
                    "Kampala"
                } else if (i < 6) {
                    "Wandegeya"
                } else if (i < 8) {
                    "Kikoni"
                } else {
                    "Kikumikikumi"
                },
                lowestPrice = "200k",
                highestPrice = "500k",
                ownerId = "user #"
            )

            db.collection("Hostels")
                .document(hostel.hostelId)
                .set(hostel)
                .addOnSuccessListener {
                    println("Data inserted successfully")
                }.addOnFailureListener { e ->
                    println("failed" + e.message)
                }
        }
        for (i in 0..3) {
            val Manager = Manager(
                managerId = "Mng$i",
                name = "Manager $i",
                email = "manager$i@gmail.com",
                phone = "071234567$i"
            )
            db.collection("Managers")
                .document(Manager.managerId)
                .set(Manager)
                .addOnSuccessListener {
                    println("Data inserted successfully")
                }.addOnFailureListener { e ->
                    println("failed" + e.message)
                }

        }

        setContent {
            CampusNestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CampusNestApp()
                }
            }
        }

    }
}
