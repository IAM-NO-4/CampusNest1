package com.campusnest1.groupq.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.ui.theme.*
import com.campusnest1.groupq.viewmodel.HostelViewModel
import com.campusnest1.groupq.viewmodel.auth.profileViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.campusnest1.groupq.navigation.Screen
import com.campusnest1.groupq.utils.formatCurrency
import com.campusnest1.groupq.utils.getTime
import com.campusnest1.groupq.viewmodel.NotificationViewModel

@Composable
fun CampusNestApp(
    navController: NavController,
    viewModel: HostelViewModel = koinViewModel(),
    profViewModel: profileViewModel = koinViewModel(),
    notifViewModel: NotificationViewModel = koinViewModel(),
    onScroll: (Boolean) -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.fetchHostelsData()
        viewModel.loadStudentData()
        profViewModel.fetchProfileData()
    }

    val userId = profViewModel.uiState.userId
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            notifViewModel.fetchNotifications(userId = userId)
        }
    }

    var showNotificationsSheet by remember { mutableStateOf(false) }
    val uiState = profViewModel.uiState
    val hostels = viewModel.hostels
    
    // Check if there are any unread notifications
    val hasUnread = notifViewModel.notifications.any { !it.isRead }

    if (showNotificationsSheet) {
        NotificationsSheet(
            navController = navController,
            notifications = notifViewModel.notifications,
            onDismiss = { showNotificationsSheet = false },
            onDelete = { notification ->
                notifViewModel.deleteNotification(notification.notificationId)
            },
            onNotificationClick = { notification ->
                notifViewModel.markAsRead(notification.notificationId)
                notification.targetId?.let { id ->
                    when (notification.category.lowercase()) {
                        "event" -> navController.navigate("eventDetails/$id")
                        "hostel", "room availability", "booking" -> navController.navigate("hostelDetails/$id")
                    }
                    showNotificationsSheet = false
                }
            }
        )
    }

    HomeScreenContent(
        fName = uiState.fname,
        hostels = hostels,
        hasUnreadNotifications = hasUnread,
        onNotificationClick = { showNotificationsSheet = true },
        onSeeAllClick = { navController.navigate(Screen.Hostels.route) },
        savedStatus = viewModel.savedStatus,
        onToggleFavorite = { viewModel.toggleFavorite(it) },
        onCheckIfSaved = { viewModel.checkIfSaved(it) },
        onTabSelected = { category ->
            when (category) {
                "Hostels" -> navController.navigate(Screen.Hostels.route)
                "Events" -> navController.navigate(Screen.Events.route)
                else -> viewModel.setCategory(category)
            }
        },
        onNavigateToDetails = { hostelId ->
            navController.navigate("hostelDetails/$hostelId")
        },
        onSearchClick = { navController.navigate("hostels") },
        onScroll = onScroll
    )
}

@Composable
fun HomeScreenContent(
    fName: String,
    hostels: List<Hostel>,
    selectedCategory: String = "All",
    hasUnreadNotifications: Boolean = false,
    onNotificationClick: () -> Unit = {},
    onSeeAllClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    savedStatus: Map<String, Boolean> = emptyMap(),
    onToggleFavorite: (String) -> Unit = {},
    onCheckIfSaved: (String) -> Unit = {},
    onNavigateToDetails: (String) -> Unit = {},
    onTabSelected: (String) -> Unit = {},
    onScroll: (Boolean) -> Unit = {}
) {
    val categories = listOf("All", "Hostels", "Events")
    val scrollState = rememberScrollState()

    val shouldShow = !scrollState.isScrollInProgress || scrollState.value == 0
    LaunchedEffect(shouldShow) {
        onScroll(shouldShow)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        HeaderSection(
            onNotificationClick = onNotificationClick, 
            fName = fName,
            hasUnreadNotifications = hasUnreadNotifications
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SearchBar(onSearchClick = onSearchClick)
        
        Spacer(modifier = Modifier.height(20.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Button(
                    onClick = { onTabSelected(category) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) TealPrimary else Color.White,
                        contentColor = if (isSelected) Color.White else TextDark
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(text = category, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Recommended",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "Hostels",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
            TextButton(onClick = onSeeAllClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "See all",
                        color = TealPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TealPrimary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (hostels.isEmpty() && selectedCategory != "Events") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hostels available", color = TextGrey)
            }
        } else {
            HostelList(
                hostels = hostels,
                savedStatus = savedStatus,
                onToggleFavorite = onToggleFavorite,
                onCheckIfSaved = onCheckIfSaved,
                onNavigateToDetails = onNavigateToDetails
            )
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun HeaderSection( 
    onNotificationClick: () -> Unit,
    fName: String = "Alex",
    hasUnreadNotifications: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when(getTime()){
                        "Good morning","Good evening" -> Icons.Default.WbTwilight
                         else -> Icons.Default.WbSunny
                    },
                    contentDescription = null,
                    tint = Color(0xFFF2994A),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = getTime(),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextGrey
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hello, $fName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "👋",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.size(48.dp)
        ) {
            IconButton(onClick = onNotificationClick) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = TextDark
                    )
                    // Conditionally show the red dot based on hasUnreadNotifications
                    if (hasUnreadNotifications) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .offset(x = 10.dp, y = (-10).dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(onSearchClick: () -> Unit) {
    Surface(
        onClick = onSearchClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = TextGrey,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Where are you nesting tonight?",
                color = TextGrey,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun HostelList(
    hostels: List<Hostel>,
    savedStatus: Map<String, Boolean>,
    onToggleFavorite: (String) -> Unit,
    onCheckIfSaved: (String) -> Unit,
    onNavigateToDetails: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        hostels.forEach { hostel ->
            HostelCard(
                hostel = hostel,
                isSaved = savedStatus[hostel.hostelId] ?: false,
                onToggleFavorite = { onToggleFavorite(hostel.hostelId) },
                onCheckIfSaved = { onCheckIfSaved(hostel.hostelId) },
                onNavigateToDetails = { onNavigateToDetails(hostel.hostelId) }
            )
        }
    }
}

@Composable
fun HostelCard(
    hostel: Hostel,
    isSaved: Boolean,
    onToggleFavorite: () -> Unit,
    onCheckIfSaved: () -> Unit,
    onNavigateToDetails: () -> Unit
) {
    LaunchedEffect(hostel.hostelId) {
        onCheckIfSaved()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
            ) {
                AsyncImage(
                    model = hostel.imageUrl,
                    contentDescription = hostel.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    color = Color.White.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = hostel.distance,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }

                // Favorites
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .size(36.dp),
                    color = Color.White,
                    shape = CircleShape
                ) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isSaved) Color.Red else TextGrey,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hostel.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                //Rating
                HostelRating(hostel)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = TextGrey,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = hostel.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrey
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "UGX ${formatCurrency(hostel.highestPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = OrangeAccent
                    )
                    Text(
                        text = " /semester",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGrey,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                
                Button(
                    onClick = onNavigateToDetails,
                    colors = ButtonDefaults.buttonColors(containerColor = TealSecondary),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "View Details",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun HostelRating(hostel: Hostel, color: Color = OrangeAccentLight) {
    Surface(color = color , shape = RoundedCornerShape(12.dp)){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = StarYellow,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = hostel.avgRating.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1500)
@Composable
fun HomeScreenPreview() {
    CampusNestTheme {
        HomeScreenContent(
            fName = "Amir",
            hostels = MockData.mockHostels
        )
    }
}
