package com.campusnest1.groupq.ui

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.model.Room
import com.campusnest1.groupq.ui.theme.*
import com.campusnest1.groupq.utils.formatCurrency
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
        Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TealPrimary)
        }
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            HostelDetailsContent(
                hostel = hostel,
                rooms = rooms,
                isSaved = isSaved,
                viewModel = viewModel,
                onBackClick = onBackClick,
                onToggleFavorite = { viewModel.toggleFavorite(hostel.hostelId) },
                onShareClick = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out ${hostel.name} at CampusNest!")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, null))
                }
            )

            // Fixed Floating Bottom Bar
            Box(modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding()) {
                BottomBookingBar(viewModel = viewModel)
            }

            // Booking Bottom Sheet - Now shows even if no room selected manually
            if (viewModel.showBookingSheet) {
                val roomToBook = viewModel.selectedRoom ?: rooms.firstOrNull { it.isAvailable } ?: (if(rooms.isNotEmpty()) rooms[0] else Room())
                BookingBottomSheet(
                    hostel = hostel,
                    room = roomToBook,
                    onDismiss = { viewModel.updateShowBookingSheet(false) },
                    onBook = { _, _ ->
                        viewModel.updateShowBookingSheet(false)
                        viewModel.contactManager(hostel.managerId, context)
                    }
                )
            }
        }
    }
}

@Composable
fun HostelDetailsContent(
    hostel: Hostel,
    rooms: List<Room>,
    isSaved: Boolean,
    viewModel: HostelViewModel,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShareClick: () -> Unit
) {
    val isDescriptionExpanded = viewModel.isDescriptionExpanded
    val selectedRoom = viewModel.selectedRoom

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HostelHeaderImage(
                imageUrl = hostel.imageUrl,
                isSaved = isSaved,
                onBackClick = onBackClick,
                onToggleFavorite = onToggleFavorite,
                onShareClick = onShareClick
            )
        }

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
                    HostelRating(hostel, color = OrangeAccentLight)
                }

                HostelAddress(hostel)

                Spacer(modifier = Modifier.height(24.dp))
                // Explicitly setting TextDark color for headers
                Text(text = "About", style = MaterialTheme.typography.titleMedium, color = TextDark, fontWeight = FontWeight.Bold)
                Text(
                    text = hostel.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4F4F4F),
                    modifier = Modifier.padding(top = 8.dp),
                    maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )
                TextButton(onClick = { viewModel.toggleDescriptionExpanded() }, contentPadding = PaddingValues(0.dp)) {
                    Text(text = if (isDescriptionExpanded) "Read less" else "Read more", color = TealPrimary, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Amenities", style = MaterialTheme.typography.titleMedium, color = TextDark, fontWeight = FontWeight.Bold)
                AmenitiesList(hostel.amenities)

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Available Rooms", fontWeight = FontWeight.Bold, color = TextDark, style = MaterialTheme.typography.titleMedium)
            }
        }

        itemsIndexed(rooms) { _, room ->
            RoomCard(
                room = room,
                isSelected = selectedRoom?.roomId == room.roomId,
                onSelect = { viewModel.selectRoom(room) }
            )
        }
        item { Spacer(modifier = Modifier.height(120.dp)) }
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
    Box(modifier = Modifier.height(320.dp).fillMaxWidth()) {
        AsyncImage(model = imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.3f)) {
                IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.3f)) {
                    IconButton(onClick = onShareClick) { Icon(Icons.Default.Share, null, tint = Color.White) }
                }
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
    }
}

@Composable
fun RoomCard(room: Room, isSelected: Boolean, onSelect: () -> Unit) {
    val isAvailable = room.isAvailable && !room.status.contains("Full", ignoreCase = true)
    val statusColor = if (!isAvailable) Color.Red else Color(0xFFF2994A)
    val statusBg = if (!isAvailable) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, if (isSelected) TealPrimary else Color.Transparent)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)){
            if(isSelected){
                Surface(modifier = Modifier.width(6.dp).fillMaxHeight(), color = TealPrimary){}
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = room.type, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = if (isAvailable) TextDark else TextGrey)
                    if (room.status.isNotEmpty()) {
                        Surface(color = statusBg, shape = RoundedCornerShape(8.dp)) {
                            Text(text = room.status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bed, null, tint = TextGrey, modifier = Modifier.size(16.dp))
                    Text(" ${room.beds} Bed", color = TextGrey, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Person, null, tint = TextGrey, modifier = Modifier.size(16.dp))
                    Text(" ${room.capacity} Person", color = TextGrey, fontSize = 13.sp)
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Cost", color = TextGrey, fontSize = 12.sp)
                        Text(text = "UGX ${formatCurrency(room.price)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (isAvailable) TextDark else TextGrey)
                    }
                    Button(
                        onClick = onSelect, 
                        enabled = isAvailable, 
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, disabledContainerColor = Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isSelected) "Selected" else if (isAvailable) "Select" else "Full")
                    }
                }
            }
        }
    }
}

@Composable
fun AmenitiesList(amenities: List<String>) {
    LazyRow(contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        itemsIndexed(amenities) { _, amenity ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = RoundedCornerShape(12.dp), color = GreySurface, modifier = Modifier.size(56.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when(amenity) {
                                "Free Wi-Fi" -> Icons.Default.Wifi
                                "Gym" -> Icons.Default.FitnessCenter
                                "Security" -> Icons.Default.Security
                                else -> Icons.Default.Done
                            },
                            contentDescription = null,
                            tint = TealPrimary
                        )
                    }
                }
                Text(text = amenity, fontSize = 11.sp, color = TextDark, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun BottomBookingBar(viewModel: HostelViewModel) {
    Surface(shadowElevation = 12.dp, color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { viewModel.updateShowBookingSheet(true) },
            modifier = Modifier.padding(20.dp).fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Book Viewing", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White)
        }
    }
}

@Composable
fun HostelAddress(hostel: Hostel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.LocationOn, null, tint = TealPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = hostel.location, style = MaterialTheme.typography.bodyMedium, color = TextGrey)
    }
}
