package com.campusnest1.groupq.entities

sealed class AuthState {
    object Idle: AuthState()
    object Loading: AuthState()
    object Authenticated: AuthState()
    data class Error(val message: String): AuthState()
}