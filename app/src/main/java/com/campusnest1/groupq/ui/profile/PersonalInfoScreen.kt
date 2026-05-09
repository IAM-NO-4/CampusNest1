package com.campusnest1.groupq.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.campusnest1.groupq.navigation.Screen
import com.campusnest1.groupq.ui.theme.BorderLight
import com.campusnest1.groupq.ui.theme.ErrorRed
import com.campusnest1.groupq.ui.theme.LightBlue
import com.campusnest1.groupq.ui.theme.SurfaceWhite
import com.campusnest1.groupq.ui.theme.TealAccent
import com.campusnest1.groupq.ui.theme.TextGrey
import com.campusnest1.groupq.ui.theme.TextPrimary
import com.campusnest1.groupq.viewmodel.auth.profileViewModel

@Composable
fun PersonalInfoScreen(
    navController: NavController,
    profileView: profileViewModel = koinViewModel()
) {
    val profileState = profileView.uiState

    LaunchedEffect(Unit) {
        profileView.fetchProfileData()
    }

    // Only show full-screen loading if we have NO data yet
    val isInitialLoading = profileState.isLoading && profileState.fname.isEmpty()

    if (isInitialLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TealAccent)
        }
    } else {
        PersonalInfoContent(
            profileState = profileState,
            onEditClick = { navController.navigate(Screen.ProfileSettings.route) }
        )
    }
}

@Composable
fun PersonalInfoContent(
    profileState: ProfileUiState,
    onEditClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(listOf(LightBlue, SurfaceWhite))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Personal Info",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TealAccent
            )

            Spacer(modifier = Modifier.height(20.dp))

            // User Info
            ProfileInfoDisplay(label = "First Name", value = profileState.fname)
            ProfileInfoDisplay(label = "Last Name", value = profileState.lname)
            ProfileInfoDisplay(label = "Email Address", value = profileState.email)
            ProfileInfoDisplay(label = "Phone Number", value = profileState.phone)
            
            // Academic Info
            ProfileInfoDisplay(label = "Course", value = profileState.course?.ifEmpty { "Not set" } ?: "Not set")
            ProfileInfoDisplay(label = "Year of Study", value = profileState.yearOfStudy?.ifEmpty { "Not set" } ?: "Not set")
            ProfileInfoDisplay(label = "Current Hostel", value = profileState.currentHostel.ifEmpty { "Not set" })

            if (profileState.error != null) {
                Text(text = profileState.error, color = ErrorRed, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
            ) {
                Text(
                    text = "Edit Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ProfileInfoDisplay(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = TextGrey
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 16.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = BorderLight,
                unfocusedBorderColor = BorderLight,
                disabledBorderColor = BorderLight,
                cursorColor = TealAccent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PersonalInfoPreview() {
    PersonalInfoContent(
        profileState = ProfileUiState(
            fname = "Alex",
            lname = "Johnson",
            email = "alex@campus.edu",
            phone = "+254 700 000 000",
            course = "Bsc. Computer Science",
            yearOfStudy = "Year 3",
            currentHostel = "Lakeside"
        )
    )
}
