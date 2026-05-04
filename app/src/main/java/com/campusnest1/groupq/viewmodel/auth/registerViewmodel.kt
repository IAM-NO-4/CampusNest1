package com.campusnest1.groupq.viewmodel.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.campusnest1.groupq.auth1.Authrepo
import com.campusnest1.groupq.auth1.RegisterUiState
import com.campusnest1.groupq.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class registerViewModel : ViewModel() {
    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun onFNameChange(fname: String) {
        uiState = uiState.copy(
            fname = fname,
            fnameError = if (fname.isEmpty()) "First name cannot be empty" else null
        )
    }

    fun onLNameChange(lname: String) {
        uiState = uiState.copy(
            lname = lname,
            lnameError = if (lname.isEmpty()) "Last name cannot be empty" else null
        )
    }

    fun onEmailChange(email: String) {
        val isValid = android.util.Patterns
            .EMAIL_ADDRESS.matcher(email).matches()
        uiState = uiState.copy(
            email = email,
            emailError = if (!isValid) "Invalid email" else null
        )
    }

    fun onPasswordChange(pass: String) {
        val error = when {
            pass.length < 6 -> "At least 6 characters"
            pass.all { it.isDigit() } -> "Cannot be only digits"
            !pass.any { it.isLetter() } -> "Must include a letter"
            else -> null
        }
        uiState = uiState.copy(
            password = pass,
            passwordError = error
        )
    }

    fun onPhoneChange(phone: String) {
        uiState = uiState.copy(phone = phone)
    }

    fun togglePasswordVisibility() {
        uiState = uiState.copy(passwordVisible = !uiState.passwordVisible)
    }

    private val repository = Authrepo()
    val db = Firebase.firestore

    fun register() {
        val state = uiState
        if (state.email.isEmpty() || state.password.isEmpty()) {
            uiState = state.copy(error = "Email and password cannot be empty")
            return
        }

        if (!isStrongPassword(state.password)) {
            uiState = state.copy(error = "Weak password")
            return
        }

        uiState = state.copy(isLoading = true, error = null)

        repository.register(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid
                    if (uid != null) {
                        val newUser = User(
                            userId = uid,
                            fname = state.fname,
                            lname = state.lname,
                            email = state.email,
                            phone = state.phone
                        )
                        db.collection("User")
                            .document(uid)
                            .set(newUser)
                            .addOnSuccessListener {
                                uiState = uiState.copy(
                                    isLoading = false,
                                    error = null,
                                    isSuccess = true
                                )
                            }
                            .addOnFailureListener {
                                uiState = uiState.copy(
                                    isLoading = false,
                                    error = it.message
                                )
                            }
                    }
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = task.exception?.message
                    )
                }
            }
    }

    fun isStrongPassword(password: String): Boolean {
        return password.length >= 6 &&
                password.any { it.isDigit() } &&
                password.any { it.isLetter() } &&
                !password.all { it.isDigit() }
    }

    fun getPasswordStrength(password: String): String {
        return when {
            password.length < 6 -> "Weak"
            password.all { it.isDigit() } -> "Weak"
            password.any { it.isLetter() } && password.any { it.isDigit() } -> "Strong"
            else -> "Medium"
        }
    }

    fun isFormValid(): Boolean {
        val s = uiState
        return s.fname.isNotBlank() &&
                s.lname.isNotBlank() &&
                s.email.isNotBlank() &&
                s.password.isNotBlank() &&
                s.phone.isNotBlank() &&
                s.fnameError == null &&
                s.lnameError == null &&
                s.emailError == null &&
                s.passwordError == null
    }
}
