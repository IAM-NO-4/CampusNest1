package com.campusnest1.groupq.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(
        "home",
        Icons.Outlined.Home,
        "Home"
    )

    object Hostels : BottomNavItem(
        "hostels",
        Icons.Outlined.Apartment,
        "Hostels"
    )

    object Profile : BottomNavItem(
        "profile",
        Icons.Outlined.Person,
        "Profile"
    )
}