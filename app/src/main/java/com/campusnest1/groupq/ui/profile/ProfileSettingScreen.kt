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
import com.campusnest1.groupq.auth1.RegisterUiState
import com.campusnest1.groupq.viewmodel.auth.profileViewModel
import com.campusnest1.groupq.viewmodel.auth.registerViewModel

@Composable
fun ProfileSettingsScreen(
    navController: NavController? = null,
    profileView: profileViewModel = viewModel(),
    regView: registerViewModel = viewModel()
) {
    val profileState = profileView.uiState
    val userState = regView.uiState

    ProfileSettingsContent(
        profileState = profileState,
        userState = userState,
        onNameChange = { regView.onNameChange(it) },
        onPhoneChange = { regView.onPhoneChange(it) },
        onEmailChange = { regView.onEmailChange(it) },
        onCourseChange = { profileView.onCourseChange(it) },
        onYearChange = { profileView.onYearChange(it) },
        onHostelChange = { profileView.onHostelChange(it) },
        onSaveButton = { profileView.saveProfile(userState.name,
            userState.email, userState.phone,
            onSuccess = { navController?.navigate("profile"){
                popUpTo("profile") { inclusive= true }
            } }) },
        onRoomNoChange = { profileView.onRoomNoChange(it) }
    )
}

@Composable
fun ProfileSettingsContent(
    profileState: ProfileUiState,
    userState: RegisterUiState,
    onNameChange: (String) -> Unit = {},
    onPhoneChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onCourseChange: (String) -> Unit = {},
    onYearChange: (String) -> Unit = {},
    onRoomNoChange: (String) -> Unit = {},
    onSaveButton: () -> Unit = {},
    onHostelChange: (String) -> Unit = {}
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
                text = "Edit Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00A3A3)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Personal Info
            ProfileInputField(label = "Name", value = userState.name, onValueChange = onNameChange)
            ProfileInputField(label = "Email", value = userState.email, onValueChange = onEmailChange)
            ProfileInputField(label = "new Phone", value = userState.phone, onValueChange = onPhoneChange)
            
            // Academic Info
            ProfileInputField(label = "Course", value = profileState.course ?: "", onValueChange = onCourseChange)
            ProfileInputField(label = "Year of Study", value = profileState.yearOfStudy ?: "", onValueChange = onYearChange)
            ProfileInputField(label = "Current Hostel", value = profileState.currentHostel ?: "", onValueChange = onHostelChange)
            ProfileInputField(label = "Room Number", value = profileState.currentRoomNo ?: "", onValueChange = onRoomNoChange)

            Spacer(modifier = Modifier.height(30.dp))
            
            Button(
                onClick = {
                    onSaveButton()
                },
                enabled = profileState.isSuccess,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A3A3))
            ) {

                if (profileState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Enter your $label", color = Color.Gray,
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
}

@Preview(showBackground = true)
@Composable
fun proSettingsPreview() {
    ProfileSettingsContent(
        profileState = ProfileUiState(course = "Computer Science", yearOfStudy = "Year 3"),
        userState = RegisterUiState(name = "Alex Johnson", email = "alex@campus.edu")
    )
}
