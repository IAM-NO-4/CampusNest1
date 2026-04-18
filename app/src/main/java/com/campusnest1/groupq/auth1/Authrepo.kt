package com.campusnest1.groupq.auth1
import com.google.firebase.auth.FirebaseAuth

class Authrepo {
    private val auth = FirebaseAuth.getInstance()

    fun register(emaill: String, password:String) =
        auth.createUserWithEmailAndPassword(emaill, password)

    fun login(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)

    fun logout() = auth.signOut()

    fun getCurrentUser() = auth.currentUser
}
