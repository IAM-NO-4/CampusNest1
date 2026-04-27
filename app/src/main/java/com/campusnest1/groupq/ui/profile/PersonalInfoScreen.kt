package com.campusnest1.groupq.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.campusnest1.groupq.navigation.Screen
import com.campusnest1.groupq.viewmodel.auth.profileViewModel

@Composable
fun PersonalInfoScreen(
    navController: NavController,
    profileView: profileViewModel = viewModel()
) {
    val profileState = profileView.uiState

    PersonalInfoContent(
        profileState = profileState,
        onEditClick = { navController.navigate(Screen.ProfileSettings) }
    )
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
                brush = Brush.verticalGradient(listOf(Color(0xFFE0F7FA), Color.White))
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
                color = Color(0xFF00A3A3)
            )

            Spacer(modifier = Modifier.height(20.dp))


            // User Info
            ProfileInfoDisplay(label = "First Name", value = profileState.fname)
            ProfileInfoDisplay(label = "Last Name", value = profileState.lname)
            ProfileInfoDisplay(label = "Email Address", value = profileState.email)
            ProfileInfoDisplay(label = "Phone Number", value = profileState.phone)
            
            // Academic Info
            ProfileInfoDisplay(label = "Course", value = profileState.course ?: "Not set")
            ProfileInfoDisplay(label = "Year of Study", value = profileState.yearOfStudy ?: "Not set")
            ProfileInfoDisplay(label = "Current Hostel", value = profileState.currentHostel ?: "Not set")

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A3A3))
            ) {
                Text(
                    text = "Edit Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
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
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0E0E0),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                disabledBorderColor = Color(0xFFE0E0E0),
                cursorColor = Color(0xFF00A3A3)
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
