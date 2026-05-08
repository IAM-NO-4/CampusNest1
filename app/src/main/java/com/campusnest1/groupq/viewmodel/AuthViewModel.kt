package com.campusnest1.groupq.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusnest1.groupq.data.AuthRepository
import com.campusnest1.groupq.data.AuthImplementationRepository
import com.campusnest1.groupq.model.Profile
import com.campusnest1.groupq.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
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

    // Login Form State
    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _passwordVisible = mutableStateOf(false)
    val passwordVisible: State<Boolean> = _passwordVisible

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

            result.onSuccess { loggedInUser ->
                _user.value = loggedInUser // Manually update to trigger navigation instantly
                _isLoading.value = false
            }.onFailure { exception ->
                _isLoading.value = false
                _errorMessage.value = exception.message ?: "Login failed. Please try again."
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _errorMessage.value = "Please enter your email address."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.resetPassword(email)
            result.onSuccess {
                _isLoading.value = false
                _errorMessage.value = "Password reset email sent."
            }.onFailure { exception ->
                _isLoading.value = false
                _errorMessage.value = exception.message ?: "Failed to send reset email."
            }
        }
    }

    fun signUp(email: String, pass: String, fname: String, lname: String, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.signUp(email, pass, fname, lname, phone)
            val userId = FirebaseAuth.getInstance().currentUser?.uid


            result.onSuccess {
                _isLoading.value = false

                val newProfile = Profile(
                    userId = userId ?: "",
                    fname = fname,
                    lname = lname,
                    email = email,
                    phone = phone,
                    profileImageUrl = "",
                    course = "",
                    yearOfStudy = "",
                    currentHostel = "",
                    currentRoomNo = "",
                    favHostels = ""
                )
                val db = Firebase.firestore
                db.collection("profiles").document(userId ?: "")
                    .set(newProfile)

            }.onFailure { exception ->
                _isLoading.value = false
                _errorMessage.value = exception.message ?: "Sign up failed."
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _user.value = null // Clear immediately for UI feedback
        }
    }

    fun onEmailChange(newValue: String) {
        _email.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    // Helper to clear errors when the user starts typing again
    fun clearError() {
        _errorMessage.value = null
    }
}
