package com.campusnest1.groupq.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.campusnest1.groupq.auth1.Authrepo
import com.campusnest1.groupq.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class registerViewModel : ViewModel() {

    private val repository = Authrepo()
    val db = Firebase.firestore

    fun register(email: String, password: String, user: User) {
        if (email.isEmpty() || password.isEmpty()) {
            println("Email or password is empty")
            return //wil show error message
        }
        if (password.length < 6 || password.all { it.isDigit() }) {
            println("use a longer password with a combination of letters and numbers")
            return   // will show error msg in composable
        }

        repository.register(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val FirebaseUser = task.result.user
                    val uid = FirebaseUser?.uid

                    if(uid!=null){
                        db.collection("User")
                            .document(uid).set(user)
                    }
                    println("Registration successful")
                    //wil redirect the user to homescreen

                } else {
                    println("Registration failed")
                    val error = task.exception?.message
                }
            }
    }



    fun logout() {
        repository.logout()
        // will redirect the user to login screen and clear back stack
    }

}