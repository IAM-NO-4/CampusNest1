package com.campusnest1.groupq.auth1;

data class RegisterUiState(
    val fname: String = "",
    val lname: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    var passwordVisible: Boolean = false,

    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
)
