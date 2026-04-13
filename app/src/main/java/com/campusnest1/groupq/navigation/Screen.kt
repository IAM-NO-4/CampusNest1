package com.campusnest1.groupq.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Details : Screen("details")
}