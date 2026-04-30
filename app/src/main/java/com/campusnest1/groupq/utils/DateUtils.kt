package com.campusnest1.groupq.utils

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Formats a date string from yyyy-MM-dd to "EEEE, d MMMM, yyyy"
 * (e.g., 2026-05-15 to Friday, 15 May, 2026)
 */
fun formatEventDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, d MMMM, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

/* Formatting the time from 24hr to 12hr system*/
fun formatEventTime(timeString: String): String {
    return try {
        //Input in 24hr format
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        //Output
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val time = inputFormat.parse(timeString)
        time?.let {outputFormat.format(it)} ?: timeString
    } catch (e: Exception) {
        timeString
    }
}