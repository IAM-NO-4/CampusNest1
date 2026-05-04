package com.campusnest1.groupq.ui

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.model.Room
import com.campusnest1.groupq.ui.BookingBottomSheet
import com.campusnest1.groupq.ui.HostelRating
import com.campusnest1.groupq.ui.MockData
import com.campusnest1.groupq.ui.theme.*
import com.campusnest1.groupq.viewmodel.HostelViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HostelDetailsScreen(
    hostelId: String,
    viewModel: HostelViewModel = koinViewModel(),
    onBackClick: () -> Unit = {}
) {
    val hostel = viewModel.currentHostel
    val rooms = viewModel.currentRooms
    val isSaved = viewModel.savedStatus[hostelId] ?: false
    val context = LocalContext.current

    LaunchedEffect(hostelId) {
        viewModel.fetchHostelDetails(hostelId)
        viewModel.checkIfSaved(hostelId)
    }

    if (hostel == null || viewModel.isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TealPrimary)
        }
    } else {
        HostelDetailsContent(
            hostel = hostel,
            rooms = rooms,
            isSaved = isSaved,
            onBackClick = onBackClick,
            onToggleFavorite = { viewModel.toggleFavorite(hostel.hostelId) },
            onShareClick = {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out ${hostel.name} in ${hostel.location}! Prices starting from UGX ${hostel.lowestPrice}. Download CampusNest to see more.")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }
        )
    }
}

@Composable
fun HostelDetailsContent(
    hostel: Hostel,
    rooms: List<Room>,
    isSaved: Boolean,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShareClick: () -> Unit
) {
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    var selectedRoom by remember { 
        mutableStateOf<Room?>(rooms.firstOrNull { it.isAvailable && !it.status.contains("Full", ignoreCase = true) }) 
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomBookingBar(hostel = hostel, selectedRoom = selectedRoom)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header Image Section
            item {
                HostelHeaderImage(
                    imageUrl = hostel.imageUrl,
                    isSaved = isSaved,
                    onBackClick = onBackClick,
                    onToggleFavorite = onToggleFavorite,
                    onShareClick = onShareClick
                )
            }

            // Details Content
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = hostel.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        // This component is defined in HomeScreen.kt but shared in the same package
                        HostelRating(hostel, color = OrangeAccentLight)
                    }

                    HostelAddress(hostel)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        text = hostel.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4F4F4F),
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    TextButton(
                        onClick = { isDescriptionExpanded = !isDescriptionExpanded },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = if (isDescriptionExpanded) "Read less" else "Read more",
                            color = Color(0xFF1BAFA9),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Amenities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    AmenitiesList(hostel.amenities)

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available Rooms",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextDark
                        )
                        Text(
                            text = "${rooms.size} Options",
                            color = TextGrey,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Room Cards
            itemsIndexed(rooms) { _, room ->
                RoomCard(
                    room = room,
                    isSelected = selectedRoom?.roomId == room.roomId,
                    onSelect = { if (room.isAvailable) selectedRoom = room }
                )
            }
            
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun HostelAddress(hostel: Hostel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = TealPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = hostel.location.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey
        )
    }
}

@Composable
fun HostelHeaderImage(
    imageUrl: String,
    isSaved: Boolean,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShareClick: () -> Unit
) {
    Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            //Back Button
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.3f)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
            }

            //Share Button
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.3f)) {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, null, tint = Color.White)
                    }
                }

                //Favorites
                Surface(shape = CircleShape, color = Color.White) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isSaved) Color.Red else Color.LightGray
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            color = Color.Black.copy(alpha = 0.5f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Image, null, tint = Color.White, modifier = Modifier.size(14.dp))
                Text(" 1/12", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RoomCard(
    room: Room,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {}
) {
    val isAvailable = room.isAvailable && !room.status.contains("Full", ignoreCase = true)
    val statusColor = if (!isAvailable) Color.Red else Color(0xFFF2994A)
    val statusBg = if (!isAvailable) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)
    val borderColor = if (isSelected) TealPrimary else if (room.status.isNotEmpty()) statusBg else Color.Transparent

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)){
            if(isSelected){
                Surface(
                    modifier = Modifier.width(6.dp).fillMaxHeight(),
                    color = TealPrimary
                ){}
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.type,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isAvailable) TextDark else TextGrey
                    )
                    if (room.status.isNotEmpty()) {
                        Surface(color = statusBg, shape = RoundedCornerShape(12.dp)) {
                            Text(
                                text = room.status,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Bed, null, tint = TextGrey, modifier = Modifier.size(16.dp))
                    Text(" ${room.beds} Bed", color = TextGrey, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Person, null, tint = TextGrey, modifier = Modifier.size(16.dp))
                    Text(" ${room.capacity} Person", color = TextGrey, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Monthly Rent", color = TextGrey, fontSize = 12.sp)
                        Text(
                            text = "UGX ${room.price.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isAvailable) TextDark else TextGrey
                        )
                    }
                    Button(
                        onClick = onSelect,
                        enabled = isAvailable,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isAvailable) "Select" else "Full")
                    }
                }
            }
        }
    }
}

@Composable
fun AmenitiesList(amenities: List<String>) {
    LazyRow(
        contentPadding = PaddingValues(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(amenities) { index, amenity ->
            val isSelected = index == 0
            val bgColor = if (isSelected) TealSecondary else GreySurface
            val iconColor = if (isSelected) TealPrimary else TextGrey
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = bgColor,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when(amenity) {
                                "Free Wi-Fi" -> Icons.Default.Wifi
                                "Gym" -> Icons.Default.FitnessCenter
                                "Security" -> Icons.Default.Security
                                else -> Icons.Default.Done
                            },
                            contentDescription = null,
                            tint = iconColor
                        )
                    }
                }
                Text(
                    text = amenity,
                    fontSize = 11.sp,
                    color = TextDark,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BottomBookingBar(hostel: Hostel, selectedRoom: Room?) {
    var showBookingSheet by remember { mutableStateOf(false) }

    if (showBookingSheet && selectedRoom != null){
        BookingBottomSheet(
            hostel = hostel,
            room = selectedRoom,
            onDismiss = { showBookingSheet = false },
            onBook = { _, _ ->
                showBookingSheet = false // Close after booking
            }
        )
    }

    val displayPrice = selectedRoom?.price?.toInt()?.toString() ?: hostel.lowestPrice

    Surface(
        shadowElevation = 16.dp,
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (selectedRoom != null) "Room Price" else "Starts from",
                    color = TextGrey,
                    fontSize = 14.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("UGX $displayPrice", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextDark)
                    Text("/mo", color = TextGrey, fontSize = 16.sp, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
            Button(
                onClick = { showBookingSheet = true },
                enabled = selectedRoom != null,
                modifier = Modifier.height(60.dp).fillMaxWidth(0.75f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF2994A),
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (selectedRoom != null) "Book Viewing" else "Select a Room",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    if (selectedRoom != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HostelDetailsScreenPreview() {
    CampusNestTheme {
        HostelDetailsContent(
            hostel = MockData.mockHostels[0],
            rooms = MockData.mockRooms,
            isSaved = false,
            onBackClick = {},
            onToggleFavorite = {},
            onShareClick = {}
        )
    }
}
