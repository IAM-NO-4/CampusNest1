package com.campusnest1.groupq.data

import com.google.firebase.auth.FirebaseAuth
import com.campusnest1.groupq.data.AuthRepository
import com.campusnest1.groupq.model.Profile
import com.campusnest1.groupq.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthImplementationRepository (
    private val firebaseAuth: FirebaseAuth
) : AuthRepository{
    override val authState: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            // Convert FirebaseUser to your custom User entity
            val user = auth.currentUser?.let {
                User(userId = it.uid, email = it.email ?: "")
            }
            trySend(user)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val user = User(userId = result.user?.uid?: "", email = email)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): User? {
        val fbUser = firebaseAuth.currentUser
        return fbUser?.let { User(userId = it.uid, email = it.email?: "")}
    }

    override suspend fun signUp(email: String, pass: String, fname: String, lname: String, phone: String): Result<Unit> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            
            val user = User(
                userId = userId,
                fname = fname,
                lname = lname,
                email = email,
                phone = phone
            )

            val profile = Profile(
                userId = userId,
                fname = fname,
                lname = lname,
                email = email,
                phone = phone
            )

            coroutineScope {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val userTask = async { db.collection("users").document(userId).set(user).await() }
                val profileTask = async { db.collection("profiles").document(userId).set(profile).await() }
                userTask.await()
                profileTask.await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}