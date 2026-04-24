package com.campusnest1.groupq.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavHostController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Hostels,
        BottomNavItem.Profile
    )

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo("home")
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(item.icon, contentDescription = item.label)
                },
                label = {
                    Text(item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00A86B),
                    selectedTextColor = Color(0xFF00A86B),
                    indicatorColor = Color(0x1400A86B),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}