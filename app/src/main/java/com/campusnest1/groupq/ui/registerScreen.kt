package com.campusnest1.groupq.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.campusnest1.groupq.auth1.RegisterUiState
import com.campusnest1.groupq.model.User
import com.campusnest1.groupq.viewmodel.AuthViewModel
import com.campusnest1.groupq.viewmodel.auth.registerViewModel

@Composable
fun registerScreen(
    navController: NavController? = null,
    viewModel: registerViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val state = viewModel.uiState
    val isLoading by authViewModel.isLoading
    val errorMessage by authViewModel.errorMessage

    RegisterScreenContent(
        state = state,
        authIsLoading = isLoading,
        authErrorMessage = errorMessage,
        onNameChange = { viewModel.onNameChange(it) },
        onEmailChange = { viewModel.onEmailChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onPhoneChange = { viewModel.onPhoneChange(it) },
        onPasswordVisibleToggle = { state.passwordVisible = !state.passwordVisible },
        onRegisterClick = { viewModel.register() },
        onLoginClick = { navController?.navigate("login") },
        getPasswordStrength = { viewModel.getPasswordStrength(it) },
        isFormValid = { viewModel.isFormValid() }
    )
}

@Composable
fun RegisterScreenContent(
    state: RegisterUiState,
    authIsLoading: Boolean,
    authErrorMessage: String?,
    onNameChange: (String) -> Unit,
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
                    colors = listOf(Color(0xFFE0F7FA), Color.White)
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

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Name",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    placeholder = {
                        Text(
                            text = "Enter your name", color = Color.Gray,
                            fontSize = 13.sp
                        )
                    },

                    isError = state.nameError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00A3A3),
                        errorBorderColor = Color.Red,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        cursorColor = Color(0xFF00A3A3)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                state.nameError?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
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
                    placeholder = {
                        Text(
                            text = "Enter your phone number", color = Color.Gray,
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
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
                    color = Color(0xFF333333)
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
            name = "John Doe",
            email = "john.doe@university.edu"
        ),
        authIsLoading = false,
        authErrorMessage = null,
        onNameChange = {},
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

