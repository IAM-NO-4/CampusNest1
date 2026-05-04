package com.campusnest1.groupq.ui

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.campusnest1.groupq.model.Event
import com.campusnest1.groupq.ui.theme.CampusNestTheme
import com.campusnest1.groupq.ui.theme.TealPrimary
import com.campusnest1.groupq.ui.theme.TealSecondary
import com.campusnest1.groupq.ui.theme.TextDark
import com.campusnest1.groupq.ui.theme.TextGrey
import com.campusnest1.groupq.utils.formatEventDate
import com.campusnest1.groupq.utils.formatEventTime
import com.campusnest1.groupq.viewmodel.EventViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri

@Composable
fun EventDetailsScreen(
    event: Event,
    viewModel: EventViewModel? = koinViewModel(),
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    LaunchedEffect(event.eventId) {
        viewModel?.checkIfSaved(event.eventId)
    }

    val isSaved = viewModel?.savedStatus?.get(event.eventId) ?: false

    Scaffold(
        bottomBar = {
            BottomEventBar(
                onCalendarClick = {
                    val intent = Intent(Intent.ACTION_INSERT).apply {
                        data = CalendarContract.Events.CONTENT_URI
                        putExtra(CalendarContract.Events.TITLE, event.title)
                        putExtra(CalendarContract.Events.DESCRIPTION, event.description)
                        putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
                    }
                    context.startActivity(intent)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                EventHeaderImage(
                    event = event,
                    isSaved = isSaved,
                    onBackClick = onBackClick,
                    onShareClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Check out this event: ${event.title} at ${event.location}!")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Event"))
                    },
                    onToggleFavorite = { viewModel?.toggleSavedEvent(event.eventId) }
                )
            }

            item {
                EventDetailCard(
                    event = event,
                    onRegisterClick = {
                        if (event.registrationUrl.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, event.registrationUrl.toUri())
                            context.startActivity(intent)
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun EventDetailCard(
    event: Event,
    onRegisterClick: () -> Unit
) {
    Column(modifier = Modifier.padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Surface(color = Color(0xFFEDF2F7), shape = RoundedCornerShape(12.dp)) {
                Text(
                    text = event.category.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color(0xFF616161),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = event.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        InfoRow(Icons.Outlined.CalendarToday, formatEventDate(event.date), "${formatEventTime(event.startTime)} - ${formatEventTime(event.endTime)}")
        InfoRow(Icons.Outlined.LocationOn, "Event Venue", event.location)
        InfoRow(Icons.Outlined.Money, "Open to ${event.attendees}", if (event.fee == "0" || event.fee.isEmpty()) "Free Entry" else "UGX ${event.fee}")

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark)
        Text(text = event.description, style = MaterialTheme.typography.bodyMedium, color = TextGrey, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Event Highlights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark)
        HighlightsList(event.highlights)
        
        Spacer(modifier = Modifier.height(16.dp))

        RegistrationBanner(onRegisterClick)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, modifier = Modifier.size(44.dp)) {
                AsyncImage(
                    model = event.eventOrganizerImageURL,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Organized by", style = MaterialTheme.typography.bodySmall, color = TextGrey)
                Text(text = event.eventOrganizer, style = MaterialTheme.typography.labelSmall, color = TextGrey)
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Surface(shape = RoundedCornerShape(12.dp), color = TealSecondary, modifier = Modifier.size(44.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
            }
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = TextDark)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = TextGrey)
        }
    }
}

@Composable
fun RegistrationBanner(onRegisterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = TealSecondary, shape = MaterialTheme.shapes.small)
            .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = CircleShape, color = TealSecondary.copy(alpha = 0.1f), modifier = Modifier.size(44.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Outlined.Language, contentDescription = "Website", tint = TealPrimary, modifier = Modifier.size(28.dp))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Registration", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = TextDark)
            Text(text = "Register online to secure your spot!", style = MaterialTheme.typography.bodySmall, color = TextGrey)
        }
        Button(
            onClick = onRegisterClick,
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = MaterialTheme.shapes.small
        ) {
            Text(text = "Register Now", style = MaterialTheme.typography.labelSmall, color = Color.White)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun HighlightsList(highlights: List<String>) {
    LazyRow(
        contentPadding = PaddingValues(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(highlights) { highlight ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Surface(shape = CircleShape, color = TealSecondary, modifier = Modifier.size(34.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null, tint = TealPrimary, modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = highlight, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun EventHeaderImage(
    event: Event,
    isSaved: Boolean,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
        AsyncImage(model = event.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.3f)) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.3f)) {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, null, tint = Color.White)
                    }
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
fun BottomEventBar(onCalendarClick: () -> Unit) {
    Surface(shadowElevation = 8.dp, color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onCalendarClick,
            modifier = Modifier.padding(20.dp).fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Add to Calendar", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventDetailsScreenPreview() {
    CampusNestTheme {
        EventDetailsScreen(
            event = Event(title = "Campus Tech Fest", location = "Main Hall", category = "Tech"),
            viewModel = null
        )
    }
}
