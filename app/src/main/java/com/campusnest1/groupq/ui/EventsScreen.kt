package com.campusnest1.groupq.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.campusnest1.groupq.model.Event
import com.campusnest1.groupq.ui.theme.BackgroundLight
import com.campusnest1.groupq.ui.theme.CampusNestTheme
import com.campusnest1.groupq.ui.theme.OrangeAccent
import com.campusnest1.groupq.ui.theme.OrangeAccentLight
import com.campusnest1.groupq.ui.theme.RedAccent
import com.campusnest1.groupq.ui.theme.TealPrimary
import com.campusnest1.groupq.ui.theme.TealSecondary
import com.campusnest1.groupq.ui.theme.TextDark
import com.campusnest1.groupq.ui.theme.TextGrey
import com.campusnest1.groupq.utils.isEventLive
import com.campusnest1.groupq.viewmodel.EventViewModel

@Composable
fun EventsScreen(
    navController: NavController,
    viewModel: EventViewModel,
    onScroll: (Boolean) -> Unit
) {
    val scrollState = rememberLazyListState()

    val shouldShow = !scrollState.isScrollInProgress || scrollState.firstVisibleItemIndex == 0
    LaunchedEffect(shouldShow) {
        onScroll(shouldShow)
    }

    EventsScreenContent(
        onEventClick = { eventId ->
            navController.navigate("eventDetails/$eventId")
        },
        events = viewModel.events.value,
        filteredEvents = viewModel.getFilteredEvents(),
        liveEvents = viewModel.getLiveEvents(),
        selectedCategory = viewModel.selectedCategory,
        onCategorySelected = { viewModel.setCategory(it) },
        isLoading = viewModel.isLoading.value,
        scrollState = scrollState
    )
}

@Composable
fun EventsScreenContent(
    onEventClick: (eventId: String) -> Unit = {},
    events: List<Event>,
    filteredEvents: List<Event> = emptyList(),
    liveEvents: List<Event> = emptyList(),
    selectedCategory: String = "All",
    onCategorySelected: (String) -> Unit = {},
    isLoading: Boolean = false,
    scrollState: LazyListState
) {
    val categories = listOf("All", "Social", "Academic", "Sports", "Art")

    Scaffold(
        containerColor = BackgroundLight
    ) { padding ->
        if (isLoading && events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealPrimary)
            }
        } else {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    EventHeaderSection()
                }

                // Show "Happening Now" list for live events only
                if (liveEvents.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = "Happening Now",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            HappeningNowList(liveEvents.take(5), onEventClick)
                        }
                    }
                }

                item {
                    //Top Tabs
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategorySelected(category) },
                                label = { Text(category, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = TextDark
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) TealPrimary else Color.LightGray.copy(
                                        alpha = 0.5f
                                    ),
                                    enabled = true,
                                    selected = isSelected,
                                    borderWidth = 1.dp
                                ),
                                shape = CircleShape
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = if (selectedCategory == "All") "Upcoming Events" else "$selectedCategory Events",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                if (filteredEvents.isEmpty()) {
                    item {
                        EmptyEventsState(category = selectedCategory)
                    }
                } else {
                    items(filteredEvents) { event ->
                        UpcomingEventItem(event, { onEventClick(event.eventId) })
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

            }
        }
    }
}

@Composable
fun EmptyEventsState(category: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.EventBusy,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextGrey.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (category == "All") "No events found" else "No $category events scheduled",
            style = MaterialTheme.typography.titleMedium,
            color = TextDark,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Check back later for new updates on campus happenings.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun HappeningNowList(events: List<Event>, onEventClick: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(events) { event ->
            EventCard(event = event,
                onClick = { onEventClick(event.eventId) }
            )
        }
    }
}

@Composable
fun EventHeaderSection() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Explore,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = TealPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Discover",
                style = MaterialTheme.typography.labelLarge,
                color = TextGrey
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Campus Events",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                color = TextDark
            )
        }
        Text(text = "🎉", fontSize = 24.sp)
    }
}

@Composable
fun UpcomingEventItem(event: Event, onBtnClick: () -> Unit) {
    val dateParts = event.date.split("-")
    val day = dateParts.getOrNull(2) ?: "00"
    val monthNum = dateParts.getOrNull(1) ?: "01"
    val month = when (monthNum) {
        "01" -> "Jan"
        "02" -> "Feb"
        "03" -> "Mar"
        "04" -> "Apr"
        "05" -> "May"
        "06" -> "Jun"
        "07" -> "Jul"
        "08" -> "Aug"
        "09" -> "Sep"
        "10" -> "Oct"
        "11" -> "Nov"
        "12" -> "Dec"
        else -> monthNum
    }


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            //Date
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = OrangeAccentLight.copy(alpha = 0.7f),
                modifier = Modifier.size(height = 60.dp, width = 55.dp),
                border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.5f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = month.uppercase(),
                        color = OrangeAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = day,
                        color = OrangeAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0xFFEDF2F7),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = event.category.uppercase(),
                        fontSize = 10.sp,
                        color = Color(0xFF4A5568),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = TextGrey,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGrey
                    )
                }
            }

            Button(
                onClick = { onBtnClick() },
                colors = ButtonDefaults.buttonColors(containerColor = TealSecondary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Details",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    val isLive = isEventLive(event.date, event.startTime, event.endTime)
    
    Card(
        modifier = Modifier.size(240.dp, 300.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Box {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            //Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )

            //Live Badge - Only shown if the event is currently live
            if (isLive) {
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    color = RedAccent,
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Sensors,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Live",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Surface(color = Color.White.copy(alpha = 0.4f), shape = CircleShape) {
                    Text(
                        text = event.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(event.location, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
fun EventsScreenPreview() {
    CampusNestTheme {
        val previewScrollState = rememberLazyListState()

        EventsScreenContent(
            events = com.campusnest1.groupq.ui.MockData.mockEvents,
            filteredEvents = com.campusnest1.groupq.ui.MockData.mockEvents,
            liveEvents = com.campusnest1.groupq.ui.MockData.mockEvents.take(2),
            scrollState = previewScrollState
        )
    }
}
