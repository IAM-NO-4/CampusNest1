package com.campusnest1.groupq.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusnest1.groupq.data.AuthRepository
import com.campusnest1.groupq.data.AuthImplementationRepository
import com.campusnest1.groupq.entities.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel(
    // We inject the repository interface here
    private val repository: AuthRepository = AuthImplementationRepository(
        firebaseAuth = FirebaseAuth.getInstance()
    )
) : ViewModel() {
    // Holds the currently logged-in user (Persistence)
    private val _user = mutableStateOf<User?>(repository.getCurrentUser())
    val user: State<User?> = _user

    // Tracks if a network request is in progress (Loading)
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Holds error messages to show the student (Errors)
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        // --- 2. PERSISTENCE (Auth State Listener) ---
        // Automatically updates the _user state whenever the login status changes
        viewModelScope.launch {
            repository.authState.collect { firebaseUser ->
                _user.value = firebaseUser
            }
        }
    }

    // --- 3. AUTHENTICATION FUNCTIONS ---

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.login(email, pass)

            result.onSuccess {
                _isLoading.value = false
                // Note: authState listener will handle updating _user automatically
            }.onFailure { exception ->
                _isLoading.value = false
                _errorMessage.value = exception.message ?: "Login failed. Please try again."
            }
        }
    }

    fun signUp(email: String, pass: String, name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.signUp(email, pass, name)

            result.onSuccess {
                _isLoading.value = false
            }.onFailure { exception ->
                _isLoading.value = false
                _errorMessage.value = exception.message ?: "Sign up failed."
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            // The authState listener in init{} will detect this and set _user to null
        }
    }

    // Helper to clear errors when the user starts typing again
    fun clearError() {
        _errorMessage.value = null
    }
}