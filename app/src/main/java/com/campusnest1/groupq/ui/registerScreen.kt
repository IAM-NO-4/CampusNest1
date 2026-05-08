package com.campusnest1.groupq.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.campusnest1.groupq.auth1.RegisterUiState
import com.campusnest1.groupq.navigation.Screen
import com.campusnest1.groupq.ui.theme.TextDark
import com.campusnest1.groupq.viewmodel.AuthViewModel

@Composable
fun registerScreen(
    navController: NavController? = null,
    authViewModel: AuthViewModel = koinViewModel(),
    onRegisterSuccess: () -> Unit = {}
) {
    var state by remember { mutableStateOf(RegisterUiState()) }
    val authIsLoading by authViewModel.isLoading
    val authErrorMessage by authViewModel.errorMessage
    val authUser by authViewModel.user

    LaunchedEffect(authUser) {
        if (authUser != null) {
            onRegisterSuccess()
        }
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
        return state.fname.isNotBlank() &&
                state.lname.isNotBlank() &&
                state.email.isNotBlank() &&
                state.password.isNotBlank() &&
                state.phone.isNotBlank() &&
                state.fnameError == null &&
                state.lnameError == null &&
                state.emailError == null &&
                state.passwordError == null
    }

    RegisterScreenContent(
        state = state,
        authIsLoading = authIsLoading,
        authErrorMessage = authErrorMessage,
        onFNameChange = { fname ->
            state = state.copy(
                fname = fname,
                fnameError = if (fname.isEmpty()) "First name cannot be empty" else null
            )
        },
        onLNameChange = { lname ->
            state = state.copy(
                lname = lname,
                lnameError = if (lname.isEmpty()) "Last name cannot be empty" else null
            )
        },
        onEmailChange = { email ->
            val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            state = state.copy(
                email = email,
                emailError = if (!isValid) "Invalid email" else null
            )
        },
        onPasswordChange = { pass ->
            val error = when {
                pass.length < 6 -> "At least 6 characters"
                pass.all { it.isDigit() } -> "Cannot be only digits"
                !pass.any { it.isLetter() } -> "Must include a letter"
                else -> null
            }
            state = state.copy(
                password = pass,
                passwordError = error
            )
        },
        onPhoneChange = { phone -> state = state.copy(phone = phone) },
        onPasswordVisibleToggle = { state = state.copy(passwordVisible = !state.passwordVisible) },
        onRegisterClick = {
            authViewModel.signUp(state.email, state.password, state.fname, state.lname, state.phone)
        },
        onLoginClick = { navController?.navigate(Screen.Login.route) },
        getPasswordStrength = { getPasswordStrength(it) },
        isFormValid = { isFormValid() }
    )
}

@Composable
fun RegisterScreenContent(
    state: RegisterUiState,
    authIsLoading: Boolean,
    authErrorMessage: String?,
    onFNameChange: (String) -> Unit,
    onLNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordVisibleToggle: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    getPasswordStrength: (String) -> String,
    isFormValid: () -> Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFDEEEEE), Color(0xFFF3EDE4))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo and Branding Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // CampusNest logo
                Icon(
                    imageVector = Icons.Default.Email, //place holder
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color(0xFF00A3A3)
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF00A3A3),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Campus")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFFF2994A),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Nest")
                        }
                    },
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Welcome Student!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Text(
                text = "Create an account to access CampusNest.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "First Name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.fname,
                        onValueChange = onFNameChange,
                        placeholder = {
                            Text(
                                text = "First Name", color = Color.Gray,
                                fontSize = 13.sp
                            )
                        },
                        isError = state.fnameError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedBorderColor = Color(0xFF00A3A3),
                            errorBorderColor = Color.Red,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = Color(0xFF00A3A3)
                        )
                    )
                    state.fnameError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Last Name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.lname,
                        onValueChange = onLNameChange,
                        placeholder = {
                            Text(
                                text = "Last Name", color = Color.Gray,
                                fontSize = 13.sp
                            )
                        },
                        isError = state.lnameError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedBorderColor = Color(0xFF00A3A3),
                            errorBorderColor = Color.Red,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = Color(0xFF00A3A3)
                        )
                    )
                    state.lnameError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Phone number",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.phone,
                    onValueChange = onPhoneChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Enter your phone number", color = Color.Gray,
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        focusedBorderColor = Color(0xFF00A3A3),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        cursorColor = Color(0xFF00A3A3)
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Email Input Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Student Email",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("student@campus.edu", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    isError = state.emailError != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        focusedBorderColor = Color(0xFF00A3A3),
                        errorBorderColor = Color.Red,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        cursorColor = Color(0xFF00A3A3)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                state.emailError?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password Input Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("••••••••", color = Color.Gray) },
                    isError = state.passwordError != null,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = onPasswordVisibleToggle) {
                            Icon(
                                imageVector = if (state.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        focusedBorderColor = Color(0xFF00A3A3),
                        errorBorderColor = Color.Red,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        cursorColor = Color(0xFF00A3A3)
                    ),
                    singleLine = true
                )
                val strength = getPasswordStrength(state.password)

                Text(
                    text = "Strength: $strength",
                    color = when (strength) {
                        "Weak" -> Color.Red
                        "Medium" -> Color(0xFFFFA500)
                        "Strong" -> Color.Green
                        else -> Color.Gray
                    },
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                state.passwordError?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ── Error Message from AuthViewModel ──────────────────────────
            if (authErrorMessage != null) {
                Text(
                    text = authErrorMessage,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Main Action Button (Login)
            Button(
                onClick = onRegisterClick,
                enabled = isFormValid() && !state.isLoading && !authIsLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A3A3))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.isLoading || authIsLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Registering...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Already have an account?",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            TextButton(onClick = onLoginClick) {
                Text(text = "Login", fontSize = 14.sp, color = Color(0xFF00A3A3))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreenContent(
        state = RegisterUiState(
            fname = "John",
            lname = "Doe",
            email = "john.doe@university.edu"
        ),
        authIsLoading = false,
        authErrorMessage = null,
        onFNameChange = {},
        onLNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onPhoneChange = {},
        onPasswordVisibleToggle = {},
        onRegisterClick = {},
        onLoginClick = {},
        getPasswordStrength = { "Strong" },
        isFormValid = { true }
    )
}
