package com.campusnest1.groupq.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.campusnest1.groupq.ui.theme.BorderGrey
import com.campusnest1.groupq.ui.theme.LightBlue
import com.campusnest1.groupq.ui.theme.SurfaceWhite
import com.campusnest1.groupq.ui.theme.TealAccent
import com.campusnest1.groupq.ui.theme.TextGrey
import com.campusnest1.groupq.ui.theme.TextPrimary
import com.campusnest1.groupq.viewmodel.auth.profileViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    profileViewModel: profileViewModel = koinViewModel()
) {
    val uiState = profileViewModel.uiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var pendingPreference by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        profileViewModel.fetchProfileData()
    }

    // Permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingPreference?.let { type ->
                profileViewModel.updateNotificationPreference(type, true)
                val label = when(type) {
                    "price" -> "Price alerts"
                    "event" -> "Event notifications"
                    "room" -> "Availability alerts"
                    else -> "Notifications"
                }
                scope.launch { snackbarHostState.showSnackbar("$label enabled") }
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Notifications are disabled. Please enable them in settings.")
            }
        }
        pendingPreference = null
    }

    fun checkAndRequestPermission(type: String, onPermissionAlreadyGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onPermissionAlreadyGranted()
                }
                else -> {
                    pendingPreference = type
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            onPermissionAlreadyGranted()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Notification Settings",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(LightBlue, SurfaceWhite)
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "Stay updated on what matters to you. Toggle the notifications you'd like to receive.",
                    fontSize = 14.sp,
                    color = TextGrey,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                NotificationToggleItem(
                    title = "Price Changes",
                    description = "Get notified when hostel prices drop or increase",
                    icon = Icons.Default.Sell,
                    checked = uiState.priceChangeNotify,
                    onCheckedChange = { enabled ->
                        val action = if (enabled) "enabled" else "disabled"
                        if (enabled) {
                            checkAndRequestPermission("price") {
                                profileViewModel.updateNotificationPreference("price", true)
                                scope.launch { snackbarHostState.showSnackbar("Price alerts $action") }
                            }
                        } else {
                            profileViewModel.updateNotificationPreference("price", false)
                            scope.launch { snackbarHostState.showSnackbar("Price alerts $action") }
                        }
                    }
                )

                NotificationToggleItem(
                    title = "New Events",
                    description = "Be the first to know about campus events and activities",
                    icon = Icons.Default.Event,
                    checked = uiState.newEventNotify,
                    onCheckedChange = { enabled ->
                        val action = if (enabled) { "enabled" } else { "disabled" }
                        if (enabled) {
                            checkAndRequestPermission("event") {
                                profileViewModel.updateNotificationPreference("event", true)
                                scope.launch { snackbarHostState.showSnackbar("Event notifications $action") }
                            }
                        } else {
                            profileViewModel.updateNotificationPreference("event", false)
                            scope.launch { snackbarHostState.showSnackbar("Event notifications $action") }
                        }
                    }
                )

                NotificationToggleItem(
                    title = "Room Availability",
                    description = "Alerts when rooms become available in your favorite hostels",
                    icon = Icons.Default.Hotel,
                    checked = uiState.roomAvailabilityNotify,
                    onCheckedChange = { enabled ->
                        val action = if (enabled) "enabled" else "disabled"
                        if (enabled) {
                            checkAndRequestPermission("room") {
                                profileViewModel.updateNotificationPreference("room", true)
                                scope.launch { snackbarHostState.showSnackbar("Availability alerts $action") }
                            }
                        } else {
                            profileViewModel.updateNotificationPreference("room", false)
                            scope.launch { snackbarHostState.showSnackbar("Availability alerts $action") }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, BorderGrey)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = LightBlue
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = TealAccent
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextGrey
                )
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SurfaceWhite,
                    checkedTrackColor = TealAccent
                )
            )
        }
    }
}
