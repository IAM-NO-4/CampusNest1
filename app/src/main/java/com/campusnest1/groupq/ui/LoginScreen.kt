package com.example.campusnet.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.campusnest1.groupq.viewmodel.AuthViewModel

// ── Exact brand colors from screenshot ────────────────────────────────────────
private val TealPrimary  = Color(0xFF1BAFA9)   // "Campus" text & Log In button
private val OrangeAccent = Color(0xFFF5A623)   // "Nest" text, Forgot password, Sign up
private val BgTop        = Color(0xFFDEEEEE)   // cool mint top
private val BgBottom     = Color(0xFFF3EDE4)   // warm cream bottom
private val TextDark     = Color(0xFF1A1A2E)
private val TextGray     = Color(0xFF9595AC)
private val FieldBg      = Color(0xFFFFFFFF)
private val FieldBorder  = Color(0xFFE2E2EF)
private val DividerColor = Color(0xFFDEDEEB)
private val SocialBg     = Color(0xFFF6F6FC)
private val SocialBorder = Color(0xFFE5E5F0)

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onForgotPassword: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    onAppleSignIn: () -> Unit = {},
    onSignUp: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading
    val errorMessage by authViewModel.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BgTop, BgBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(56.dp))

            // ── Building Logo ──────────────────────────────────────────────
            CampusNestLogo()

            Spacer(modifier = Modifier.height(16.dp))

            // ── "CampusNest" brand name ────────────────────────────────────
            Row {
                Text(
                    text = "Campus",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
                Text(
                    text = "Nest",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeAccent
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ── Welcome heading ────────────────────────────────────────────
            Text(
                text = "Welcome back, Student!",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Log in to access your campus dashboard.",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ── Student Email field ────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Student Email",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text("student@campus.edu", color = TextGray, fontSize = 15.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = FieldBg,
                        unfocusedContainerColor = FieldBg,
                        focusedBorderColor      = TealPrimary,
                        unfocusedBorderColor    = FieldBorder,
                        focusedTextColor        = TextDark,
                        unfocusedTextColor      = TextDark,
                        cursorColor             = TealPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Password field ─────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    TextButton(
                        onClick = onForgotPassword,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Forgot password?",
                            color = TealPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text("••••••••", color = TextGray, fontSize = 15.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = TextGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation('•'),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = FieldBg,
                        unfocusedContainerColor = FieldBg,
                        focusedBorderColor      = TealPrimary,
                        unfocusedBorderColor    = FieldBorder,
                        focusedTextColor        = TextDark,
                        unfocusedTextColor      = TextDark,
                        cursorColor             = TealPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ── Error Message ──────────────────────────────────────────────
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // ── Log In button ──────────────────────────────────────────────
            Button(
                onClick = { onLoginClick(email, password) },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 1.dp
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Log In  →",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── OR CONTINUE WITH ──────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
                Text(
                    text = "  OR CONTINUE\n  WITH  ",
                    fontSize = 10.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.6.sp,
                    lineHeight = 14.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ── Google & Apple ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedButton(
                    onClick = onGoogleSignIn,
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SocialBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = SocialBg)
                ) {
                    Text(
                        text = "Google",
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        fontSize = 15.sp
                    )
                }
                OutlinedButton(
                    onClick = onAppleSignIn,
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SocialBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = SocialBg)
                ) {
                    Text(
                        text = "Apple",
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Sign up link ───────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Don't have an account? ",
                    color = TextGray,
                    fontSize = 14.sp
                )
                TextButton(onClick = onSignUp, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        text = "Sign up",
                        color = OrangeAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ── Logo: hand-drawn building icon matching the screenshot ────────────────────
@Composable
fun CampusNestLogo() {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(width = 100.dp, height = 78.dp)
    ) {
        val w = size.width
        val teal   = TealPrimary
        val orange = OrangeAccent
        val gray   = Color(0xFFBFC8D0)

        // Gray circle (top-left moon)
        drawCircle(
            color  = gray,
            radius = 10.dp.toPx(),
            center = Offset(18.dp.toPx(), 17.dp.toPx())
        )

        // Teal horizontal stripes (3 lines = roof/sky element)
        val stripeX1 = 28.dp.toPx()
        val stripeX2 = w - 4.dp.toPx()
        listOf(8.dp.toPx(), 17.dp.toPx(), 26.dp.toPx()).forEachIndexed { i, y ->
            drawLine(
                color       = teal,
                start       = Offset(stripeX1 + i * 2.dp.toPx(), y),
                end         = Offset(stripeX2, y),
                strokeWidth = 4.dp.toPx(),
                cap         = StrokeCap.Round
            )
        }

        // Orange building body
        val bL = 10.dp.toPx()
        val bR = w - 10.dp.toPx()
        val bT = 34.dp.toPx()
        val bB = size.height - 2.dp.toPx()
        drawRect(
            color   = orange,
            topLeft = Offset(bL, bT),
            size    = androidx.compose.ui.geometry.Size(bR - bL, bB - bT)
        )

        // Teal roof triangle
        val roofPath = Path().apply {
            moveTo(w / 2f, 20.dp.toPx())
            lineTo(bL - 3.dp.toPx(), bT + 5.dp.toPx())
            lineTo(bR + 3.dp.toPx(), bT + 5.dp.toPx())
            close()
        }
        drawPath(roofPath, color = teal)

        // White window grid (2 columns × 2 rows)
        val wSz  = 11.dp.toPx()
        val wGap = 5.dp.toPx()
        val gL   = w / 2f - wSz - wGap / 2f
        val gT   = bT + 9.dp.toPx()
        for (row in 0..1) {
            for (col in 0..1) {
                drawRect(
                    color   = Color.White,
                    topLeft = Offset(gL + col * (wSz + wGap), gT + row * (wSz + wGap)),
                    size    = androidx.compose.ui.geometry.Size(wSz, wSz)
                )
            }
        }

        // Orange base/ground line
        drawLine(
            color       = orange.copy(alpha = 0.9f),
            start       = Offset(0f, bB),
            end         = Offset(w,  bB),
            strokeWidth = 3.5.dp.toPx()
        )

        // Small white door knob circle
        drawCircle(
            color  = Color.White,
            radius = 3.5.dp.toPx(),
            center = Offset(w / 2f, bB - 8.dp.toPx())
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}