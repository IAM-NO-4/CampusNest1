package com.campusnest1.groupq.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Hostels : Screen("hostels")
    object HostelDetails : Screen("hostelDetails/{hostelId}")
    object Events : Screen("events")
    object EventDetails : Screen("eventDetails/{eventId}")
    object PersonalInfo : Screen("personal_info")
    object SavedHostels : Screen("saved_hostels")
    object BookingHistory : Screen("booking_history")
    object ProfileSettings : Screen("profile_settings")
}
