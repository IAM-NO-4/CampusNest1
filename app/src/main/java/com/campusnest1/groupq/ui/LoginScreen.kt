package com.campusnest1.groupq.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.campusnest1.groupq.navigation.Screen
import com.campusnest1.groupq.ui.theme.BgBottom
import com.campusnest1.groupq.ui.theme.BgTop
import com.campusnest1.groupq.ui.theme.FieldBg
import com.campusnest1.groupq.ui.theme.FieldBorder
import com.campusnest1.groupq.ui.theme.OrangeAccent
import com.campusnest1.groupq.ui.theme.TealPrimary
import com.campusnest1.groupq.ui.theme.TextDark
import com.campusnest1.groupq.ui.theme.TextGrey
import com.campusnest1.groupq.viewmodel.AuthViewModel
import com.campusnest1.groupq.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: NavController? = null,
    authViewModel: AuthViewModel = koinViewModel(),
    onSignUp: () -> Unit = {}
) {
    val email by authViewModel.email
    val password by authViewModel.password
    val passwordVisible by authViewModel.passwordVisible
    val isLoading by authViewModel.isLoading
    val errorMessage by authViewModel.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(BgTop, BgBottom)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            CampusNestLogo()
            
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Welcome back, Student!", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextDark, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Log in to access your campus dashboard.", fontSize = 14.sp, color = TextGrey, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(36.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Student Email", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(bottom = 10.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { authViewModel.onEmailChange(it) },
                    placeholder = { Text("student@campus.edu", color = TextGrey, fontSize = 15.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = TextGrey, modifier = Modifier.size(20.dp)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FieldBg,
                        unfocusedContainerColor = FieldBg,
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = FieldBorder,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        cursorColor = TealPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().height(58.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Password", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    TextButton(onClick = { authViewModel.forgotPassword(email) }, contentPadding = PaddingValues(0.dp)) {
                        Text(text = "Forgot password?", color = TealPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { authViewModel.onPasswordChange(it) },
                    placeholder = { Text("••••••••", color = TextGrey, fontSize = 15.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextGrey, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        IconButton(onClick = { authViewModel.togglePasswordVisibility() }) {
                            Icon(imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextGrey, modifier = Modifier.size(20.dp))
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation('•'),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FieldBg,
                        unfocusedContainerColor = FieldBg,
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = FieldBorder,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        cursorColor = TealPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().height(58.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp), textAlign = TextAlign.Center)
            }

            Button(
                onClick = { authViewModel.login(email, password) },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 1.dp
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(text = "Log In  →", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Don't have an account? ", color = TextGrey, fontSize = 14.sp)
                TextButton(
                    onClick = {
                        if (navController != null) {
                            navController.navigate(Screen.Register.route)
                        } else {
                            onSignUp()
                        }
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = "Sign up", color = OrangeAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}


@Composable
fun CampusNestLogo() {
    Image(
        painter = painterResource(id = R.drawable.campus_nest_logo),
        contentDescription = "CampusNest Logo",
        modifier = Modifier
            .fillMaxWidth(0.7f) // Adjust size as needed
            .height(120.dp),
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(navController = rememberNavController())
    }
}
