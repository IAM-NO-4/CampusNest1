package com.campusnest1.groupq.model

sealed class AuthState {
    object Idle: AuthState()
    object Loading: AuthState()
    object Authenticated: AuthState()
    data class Error(val message: String): AuthState()
}