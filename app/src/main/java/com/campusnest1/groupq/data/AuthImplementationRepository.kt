package com.campusnest1.groupq.data

import com.google.firebase.auth.FirebaseAuth
import com.campusnest1.groupq.data.AuthRepository
import com.campusnest1.groupq.model.User
import kotlinx.coroutines.channels.awaitClose
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

    override suspend fun signUp(email: String, pass: String, name: String): Result<Unit> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = User(userId = result.user?.uid?: "", email = email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }
}