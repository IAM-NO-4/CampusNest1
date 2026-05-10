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
import androidx.compose.ui.draw.alpha
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
import com.campusnest1.groupq.model.Review
import com.campusnest1.groupq.ui.theme.*
import com.campusnest1.groupq.utils.formatCurrency
import com.campusnest1.groupq.viewmodel.HostelViewModel
import com.campusnest1.groupq.viewmodel.auth.profileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HostelDetailsScreen(
    hostelId: String,
    viewModel: HostelViewModel = koinViewModel(),
    profViewModel: profileViewModel = koinViewModel(),
    onBackClick: () -> Unit = {}
) {
    val hostel = viewModel.currentHostel
    val rooms = viewModel.currentRooms
    val reviews = viewModel.reviews
    val isSaved = viewModel.savedStatus[hostelId] ?: false
    val context = LocalContext.current

    LaunchedEffect(hostelId) {
        viewModel.fetchHostelDetails(hostelId)
        viewModel.checkIfSaved(hostelId)
        profViewModel.fetchProfileData()
    }

    if (hostel == null || viewModel.isLoading.value) {
        Box(modifier = Modifier.fillMaxSize().background(SurfaceWhite), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TealPrimary)
        }
    } else {
        Surface(modifier = Modifier.fillMaxSize(), color = SurfaceWhite) {
            Box(modifier = Modifier.fillMaxSize()) {
                HostelDetailsContent(
                    hostel = hostel,
                    rooms = rooms,
                    reviews = reviews,
                    isSaved = isSaved,
                    viewModel = viewModel,
                    onBackClick = onBackClick,
                    onToggleFavorite = { viewModel.toggleFavorite(hostel.hostelId) },
                    onShareClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            val displayName = hostel.name.ifBlank { "this hostel" }
                            putExtra(Intent.EXTRA_TEXT, "Check out $displayName in ${hostel.location}! Download CampusNest to see more.")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }
                )

                // Show Booking Bottom Sheet
                if (viewModel.showBookingSheet && viewModel.selectedRoom != null) {
                    BookingBottomSheet(
                        hostel = hostel,
                        room = viewModel.selectedRoom!!,
                        onDismiss = { viewModel.updateShowBookingSheet(false) },
                        onBook = { _, _ ->
                            viewModel.updateShowBookingSheet(false)
                            viewModel.contactManager(hostel.managerId, context)
                        }
                    )
                }

                //Review Bottom Sheet
                if (viewModel.showReviewDialog) {
                    ReviewBottomSheet(
                        onDismiss = { viewModel.updateShowReviewDialog(false) },
                        onSubmit = { rating, comment ->
                            viewModel.submitReview(rating, comment, profViewModel.uiState.fname)
                            viewModel.updateShowReviewDialog(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HostelDetailsContent(
    hostel: Hostel,
    rooms: List<Room>,
    reviews: List<Review>,
    isSaved: Boolean,
    viewModel: HostelViewModel,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShareClick: () -> Unit
) {
    val isDescriptionExpanded = viewModel.isDescriptionExpanded
    val selectedRoom = viewModel.selectedRoom

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            BottomBookingBar(viewModel = viewModel)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {

            //Header Image Section
            item {
                HostelHeaderImage(
                    imageUrl = hostel.imageUrl,
                    isSaved = isSaved,
                    onBackClick = onBackClick,
                    onToggleFavorite = onToggleFavorite,
                    onShareClick = onShareClick
                )
            }

            //Details Content
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = hostel.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )

                        HostelRating(hostel, color = OrangeAccentLight)
                    }

                    HostelAddress(hostel)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )

                    Text(
                        text = hostel.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDark.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    TextButton(
                        onClick = { viewModel.toggleDescriptionExpanded() },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = if (isDescriptionExpanded) "Read less" else "Read more",
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Amenities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
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
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextDark
                        )
                        Text(
                            text = if (rooms.size > 1) " ${rooms.size} Options" else " ${rooms.size} Option" ,
                            color = TextGrey,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            //Room Cards
            itemsIndexed(rooms) { _, room ->
                RoomCard(
                    room = room,
                    isSelected = selectedRoom?.roomId == room.roomId,
                    onSelect = { viewModel.selectRoom(room) }
                )
            }

            //Reviews Section
            item {
                ReviewsSection(
                    reviews = reviews,
                    avgRating = hostel.avgRating,
                    onWriteReviewClick = { viewModel.updateShowReviewDialog(true) }
                )
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun ReviewsSection(
    reviews: List<Review>,
    avgRating: Double,
    onWriteReviewClick: () -> Unit
) {
    Column(modifier = Modifier.padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reviews",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            
            TextButton(onClick = onWriteReviewClick) {
                Text("Write a review", color = TealPrimary, fontWeight = FontWeight.Bold)
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = StarYellow, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.1f", avgRating),
                fontWeight = FontWeight.Bold,
                color = TextDark,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${reviews.size} reviews)",
                color = TextGrey,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (reviews.isEmpty()) {
            Text(
                text = "No reviews yet. Be the first to rate this hostel!",
                color = TextGrey,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            reviews.take(3).forEach { review ->
                ReviewItem(review)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = TealSecondary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (review.userName.isNotEmpty()) review.userName.take(1).uppercase() else "U",
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = review.userName.ifBlank { "Student User" },
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    fontSize = 14.sp
                )
            }
            
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (index < review.rating) StarYellow else Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        
        Text(
            text = review.comment,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDark.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 8.dp)
        )
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
            text = hostel.location,
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
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() 
                .padding(16.dp),
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
                            tint = if (isSaved) RedStandard else LightGray
                        )
                    }
                }
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
    val isFull = room.status.equals("Full", ignoreCase = true) || !room.isAvailable
    val displayStatus = if (isFull) "Full" else "Available"
    val statusColor = if (isFull) ErrorRed else OrangeAccent
    val statusBg = if (isFull) RedAccentLight else OrangeAccentLight

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .alpha(if (isFull) 0.6f else 1f),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.type,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isFull) TextGrey else TextDark
                    )
                    Surface(color = statusBg, shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = displayStatus,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
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
                        Text("Cost", color = TextGrey, fontSize = 12.sp)
                        Text(
                            text = "UGX ${formatCurrency(room.price)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isFull) TextGrey else TextDark
                        )
                    }
                    Button(
                        onClick = onSelect,
                        enabled = !isFull,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isFull) "Full" else if (isSelected) "Selected" else "Select")
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
        itemsIndexed(amenities) { _, amenity ->
            val normalized = amenity.lowercase().trim()
            val icon = when {
                normalized.contains("wifi") || normalized.contains("wi-fi") -> Icons.Default.Wifi
                normalized.contains("pool") -> Icons.Default.Pool
                normalized.contains("tv") -> Icons.Default.Tv
                normalized.contains("laundry") -> Icons.Default.LocalLaundryService
                normalized.contains("cafeteria") || normalized.contains("restaurant") -> Icons.Default.Restaurant
                normalized.contains("parking") -> Icons.Default.LocalParking
                normalized.contains("gym") -> Icons.Default.FitnessCenter
                normalized.contains("security") -> Icons.Default.Security
                else -> Icons.Default.Done
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GreySurface,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = amenity,
                            tint = TealPrimary
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
fun BottomBookingBar(viewModel: HostelViewModel) {

    Surface(shadowElevation = 8.dp, color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { viewModel.updateShowBookingSheet(true) },
            modifier = Modifier.padding(20.dp).fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Book Viewing", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HostelDetailsScreenPreview() {
    CampusNestTheme {
    }
}
