package com.campusnest1.groupq.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Details : Screen("details")
    object Profile : Screen("profile")
    object PersonalInfo : Screen("personal_info")
    object ProfileSettings : Screen("profile_settings")
}