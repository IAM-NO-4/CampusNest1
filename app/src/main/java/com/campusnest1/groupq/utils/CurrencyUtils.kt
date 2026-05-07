package com.campusnest1.groupq.utils

import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: String): String {
    val cleanAmount = amount.replace(Regex("[^\\d.]"), "")
    val parsed = cleanAmount.toDoubleOrNull() ?: return amount
    
    // Using Locale.forLanguageTag to avoid constructor deprecation
    val ugandaLocale = Locale.forLanguageTag("en-UG")
    val formatter = NumberFormat.getNumberInstance(ugandaLocale)
    return formatter.format(parsed)
}
