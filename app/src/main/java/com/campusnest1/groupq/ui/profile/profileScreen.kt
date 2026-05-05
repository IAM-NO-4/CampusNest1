package com.campusnest1.groupq.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.campusnest1.groupq.navigation.Screen
import com.campusnest1.groupq.viewmodel.HostelViewModel
import com.campusnest1.groupq.viewmodel.auth.profileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    profileView: profileViewModel = koinViewModel(),
    hostelViewModel: HostelViewModel = koinViewModel()
) {
    val uiState = profileView.uiState
    val user = profileView.currentUser
    val nameParts = user?.displayName?.split(" ") ?: listOf("Student", "")
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            profileView.changeProfileImage(context, uri, uiState.userId)
        }
    }

    LaunchedEffect(Unit) {
        hostelViewModel.loadStudentData()
    }

    ProfileScreenContent(
        fname = if (uiState.fname.isNotEmpty()) uiState.fname else (nameParts.getOrNull(0) ?: "Student"),
        lname = if (uiState.lname.isNotEmpty()) uiState.lname else (nameParts.getOrNull(1) ?: ""),
        profileImageUrl = uiState.profileImageUrl,
        course = uiState.course ?: "Not Set",
        studyYear = uiState.yearOfStudy ?: "",
        currentHostel = uiState.currentHostel ?: "Not set",
        savedCount = hostelViewModel.savedHostels.size,
        bookingCount = hostelViewModel.bookingHistory.value.size,
        isNotificationsEnabled = profileView.isNotificationsEnabled.value,
        onToggleNotifications = { profileView.toggleNotifications(it) },
        onProfileImageClick = {
            launcher.launch("image/*")
        },
        navController = navController
    )
}

@Composable
fun ProfileScreenContent(
    fname: String,
    lname: String,
    profileImageUrl: String?,
    course: String,
    studyYear: String,
    currentHostel: String,
    savedCount: Int,
    bookingCount: Int,
    isNotificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    onProfileImageClick: () -> Unit,
    navController: NavController?
) {
    val scrollState = rememberScrollState()

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
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Profile Header
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    border = BorderStroke(4.dp, Color(0xFF00A3A3)),
                    color = Color.LightGray
                ) {
                    if (!profileImageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(24.dp),
                            tint = Color.Gray
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onProfileImageClick() },
                    shape = CircleShape,
                    color = Color(0xFF00A3A3),
                    border = BorderStroke(3.dp, Color.White)
                ) {

                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change photo",
                        modifier = Modifier.padding(8.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = fname,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lname,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFEFEFEF)
            ) {
                Text(
                    text = "$course : $studyYear",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Student Stats Section
            SectionHeader(title = "Student Stats")

            Spacer(modifier = Modifier.height(12.dp))

            // Current Stay Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE0F7FA)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = Color(0xFF00A3A3)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Current Stay", fontSize = 12.sp, color = Color.Gray)
                        Text(text = currentHostel, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Account Settings Section
            SectionHeader(title = "Account Settings")

            Spacer(modifier = Modifier.height(12.dp))

            SettingsItem(icon = Icons.Default.Person, label = "Personal Info",
                onItemClick = { navController?.navigate(Screen.PersonalInfo.route) })

            SettingsItem(icon = Icons.Default.History, label = "Booking History", badgeCount = bookingCount,
                onItemClick = { navController?.navigate("booking_history") })

            SettingsItem(icon = Icons.Default.Favorite, label = "Saved Hostels", badgeCount = savedCount, iconTint = Color.Red,
                onItemClick = { navController?.navigate("saved_hostels") })

            Spacer(modifier = Modifier.height(6.dp))

            // Notification Settings Switch Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Color(0xFFF5F5F5)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp),
                                tint = Color(0xFF333333)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Notification Settings",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF333333)
                        )
                    }

                    Switch(
                        checked = isNotificationsEnabled,
                        onCheckedChange = onToggleNotifications,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF00A3A3)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dark Mode
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = Color(0xFF0D1B3E)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Nightlight,
                            contentDescription = null,
                            modifier = Modifier.padding(10.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Dark Mode", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Switch to dark theme", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = false,
                        onCheckedChange = {},
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF00A3A3)
                        )
                    )
                }
            }

            // bottom spacer
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    label: String,
    badgeCount: Int? = null,
    iconTint: Color? = null,
    onItemClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFFF5F5F5)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = iconTint ?: Color(0xFF333333)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )

            if (badgeCount != null) {
                Surface(
                    modifier = Modifier.padding(end = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF2994A)
                ) {
                    Text(
                        text = badgeCount.toString(),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = { onItemClick() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
fun ProfileScreenPreview() {
    ProfileScreenContent(
        fname = "Alex",
        lname = "Muhanji",
        profileImageUrl = null,
        course = "Software Eng",
        studyYear = "2",
        currentHostel = "Lakeside Hostel",
        navController = null,
        savedCount = 4,
        bookingCount = 56,
        isNotificationsEnabled = true,
        onToggleNotifications = {},
        onProfileImageClick = {}
    )
}
