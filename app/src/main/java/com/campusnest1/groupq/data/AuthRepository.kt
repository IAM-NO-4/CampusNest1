package com.campusnest1.groupq.data

import com.campusnest1.groupq.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<User?>

    suspend fun login(email: String, pass: String): Result<User>
    suspend fun signUp(email: String, pass: String, fname: String, lname: String, phone: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun logout()
    fun getCurrentUser(): User?
}