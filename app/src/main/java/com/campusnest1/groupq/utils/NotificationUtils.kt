package com.campusnest1.groupq.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.BookOnline
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.campusnest1.groupq.ui.theme.OrangeAccent
import com.campusnest1.groupq.ui.theme.OrangeAccentLight
import com.campusnest1.groupq.ui.theme.RedAccent
import com.campusnest1.groupq.ui.theme.RedAccentLight
import com.campusnest1.groupq.ui.theme.TealPrimary
import com.campusnest1.groupq.ui.theme.TealSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getNotificationIcons(category: String): ImageVector{
    return when(category.lowercase()){
        "room availability" -> Icons.Outlined.Bed
        "price drop" -> Icons.AutoMirrored.Outlined.TrendingDown
        "price increase" -> Icons.AutoMirrored.Outlined.TrendingUp
        "event" -> Icons.Outlined.Event
        "booking" -> Icons.Outlined.BookOnline
        else -> Icons.Outlined.Notifications
    }
}

fun getNotificationColors(category: String): Pair<Color, Color>{
   return when(category.lowercase()){
       "room availability" -> Color(0xFF2E7D32) to Color(0xFFE8F5E9)
       "price drop" -> Color(0xFF2E7D32) to Color(0xFFE8F5E9)
       "price increase" -> RedAccent to RedAccentLight
       "event" -> OrangeAccent to OrangeAccentLight
       "booking" -> TealPrimary to TealSecondary
       else -> Color(0xFF616161) to Color(0xFFF5F5F5)
   }
}

/**
 * Formats a timestamp (milliseconds) into a human-readable string.
 * Example: "h:mm a" if today, "Yesterday" if yesterday, else "MMM d"
 */
fun formatTimeStamp(timeStamp: Long): String {
    val now = Calendar.getInstance()
    val timeOfNotification = Calendar.getInstance().apply {
        timeInMillis = timeStamp
    }
    return when {
        //Check if is today
        isSameDay(now, timeOfNotification) -> {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timeStamp))
        }

        //Check if it's yesterday
        isYesterday(now, timeOfNotification) -> {
            "Yesterday"
        }

        //Any other day
        else -> {
            SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timeStamp))
        }
    }
}

//Helper to check if two dates are the same day
private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean{
    return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
}

//Helper to check if notification was exactly one day ago
private fun isYesterday(now: Calendar, then: Calendar): Boolean{
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    return isSameDay(yesterday, then)
}
