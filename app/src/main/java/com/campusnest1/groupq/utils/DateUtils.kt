package com.campusnest1.groupq.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Formats a date string from yyyy-MM-dd to "EEEE, d MMMM, yyyy"
 */
fun formatEventDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, d MMMM, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString.trim())
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

fun formatEventTime(timeString: String): String {
    return try {
        val trimmed = timeString.trim()
        if (trimmed.contains("AM", ignoreCase = true) || trimmed.contains("PM", ignoreCase = true)) {
            return trimmed
        }
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val time = inputFormat.parse(trimmed)
        time?.let { outputFormat.format(it) } ?: trimmed
    } catch (e: Exception) {
        timeString
    }
}

/**
 * Helper to parse time string which could be 12h or 24h
 */
private fun parseTime(timeStr: String): Calendar? {
    val trimmed = timeStr.trim().uppercase()
    val timeFormat12 = SimpleDateFormat("h:mm a", Locale.getDefault())
    val timeFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    val time = try {
        if (trimmed.contains("AM") || trimmed.contains("PM")) {
            timeFormat12.parse(trimmed)
        } else {
            timeFormat24.parse(trimmed)
        }
    } catch (e: Exception) {
        null
    } ?: return null

    val cal = Calendar.getInstance()
    val tempCal = Calendar.getInstance()
    tempCal.time = time
    cal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY))
    cal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE))
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal
}

fun isEventLive(date: String, startTime: String, endTime: String): Boolean {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = Calendar.getInstance()
        val todayStr = dateFormat.format(now.time)

        // Only live if it's today
        if (todayStr != date.trim()) return false

        val startCal = parseTime(startTime) ?: return false
        val endCal = parseTime(endTime) ?: return false

        val isLive = now.after(startCal) && now.before(endCal)
        if (isLive) Log.d("DateUtils", "Event is LIVE right now!")
        return isLive
    } catch (e: Exception) {
        false
    }
}

/**
 * Checks if an event has already ended.
 */
fun isEventExpired(date: String, endTime: String): Boolean {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = Calendar.getInstance()
        val todayStr = dateFormat.format(now.time)

        val eventDate = dateFormat.parse(date.trim()) ?: return false
        val todayDate = dateFormat.parse(todayStr) ?: return false

        if (eventDate.before(todayDate)) return true
        if (eventDate.after(todayDate)) return false

        // If today, check if end time has passed
        val endCal = parseTime(endTime) ?: return false
        return now.after(endCal)
    } catch (e: Exception) {
        false
    }
}

fun getTime(): String {
    val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (currentTime) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}
