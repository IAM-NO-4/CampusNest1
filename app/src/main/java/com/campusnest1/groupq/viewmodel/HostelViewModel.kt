package com.campusnest1.groupq.viewmodel

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.widget.Toast.makeText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusnest1.groupq.auth1.Authrepo
import com.campusnest1.groupq.data.HostelRepository
import com.campusnest1.groupq.model.Booking
import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.model.Room
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class HostelViewModel(
    private val repository: HostelRepository,
    private val authRepository: Authrepo
) : ViewModel() {


    var hostels by mutableStateOf<List<Hostel>>(emptyList())
        private set

    var savedHostels by mutableStateOf<List<Hostel>>(emptyList())
        private set
    
    // Add state for tracking saved status of hostels by ID
    var savedStatus = mutableStateMapOf<String, Boolean>()
        private set

    var bookingHistory = mutableStateOf<List<Booking>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    // Inside HostelViewModel.kt
    var isNotificationsEnabled = mutableStateOf(true)
        private set

    val db = Firebase.firestore

    var currentHostel by mutableStateOf<Hostel?>(null)
        private set

    var currentRooms by mutableStateOf<List<Room>>(emptyList())
        private set

    // Hostel Details UI State
    var isDescriptionExpanded by mutableStateOf(false)
        private set

    var selectedRoom by mutableStateOf<Room?>(null)
        private set

    var showBookingSheet by mutableStateOf(false)
        private set

    //hostel results from firebase
    fun fetchHostelsData(){
        viewModelScope.launch {
            isLoading.value = true
            hostels = repository.getHostels()
            loadSavedStatus()
            isLoading.value = false
        }
    }

    // Load saved status for all fetched hostels at once
    private suspend fun loadSavedStatus() {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        hostels.forEach { hostel ->
            savedStatus[hostel.hostelId] = repository.isHostelSaved(userId, hostel.hostelId)
        }
    }

    fun fetchHostelDetails(hostelId: String) {
        viewModelScope.launch {
            isLoading.value = true
            currentHostel = repository.getHostelById(hostelId)
            val rooms = repository.getRoomsForHostel(hostelId)
            currentRooms = rooms
            
            // Set initial selected room
            selectedRoom = rooms.firstOrNull { it.isAvailable && !it.status.contains("Full", ignoreCase = true) }
            
            isLoading.value = false
        }
    }

    fun toggleDescriptionExpanded() {
        isDescriptionExpanded = !isDescriptionExpanded
    }

    fun selectRoom(room: Room) {
        if (room.isAvailable) {
            selectedRoom = room
        }
    }

    fun updateShowBookingSheet(show: Boolean) {
        showBookingSheet = show
    }

    // 2. Function to fetch data when the screen opens
    fun loadStudentData() {
        val userId = authRepository.getCurrentUser()?.uid ?: ""
        if (userId.isEmpty()) return

        viewModelScope.launch {
            isLoading.value = true

            // Fetch both lists in parallel
            val savedTask = async { repository.getSavedHostels(userId) }
            val bookingTask = async { repository.getBookingHistory(userId) }

            savedHostels = savedTask.await()
            bookingHistory.value = bookingTask.await()

            isLoading.value = false
        }
    }
    //for category button in homescreen real filtering
    var selectedCategory = MutableStateFlow("All")

    fun setCategory(category: String) {
        selectedCategory.value = category
    }

    //notification settings
    fun toggleNotifications(enabled: Boolean) {
        isNotificationsEnabled.value = enabled
    }

    fun toggleFavorite(hostelId: String) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        val current = savedStatus[hostelId] ?: false
        val newState = !current
        savedStatus[hostelId] = newState // Update UI immediately
        viewModelScope.launch {
            try {
                repository.toggleSavedHostel(userId, hostelId)
                // Do NOT overwrite savedStatus here — keep the optimistic state
                // Only refresh the savedHostels list silently in background
                savedHostels = repository.getSavedHostels(userId)
            } catch (e: Exception) {
                // If Firestore fails, revert back to original state
                savedStatus[hostelId] = current
            }
        }
    }

    fun checkIfSaved(hostelId: String) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            savedStatus[hostelId] = repository.isHostelSaved(userId, hostelId)
        }
    }

    fun contactManager(managerId: String, context: Context){
        viewModelScope.launch {
            val phoneNumber = repository.getManagerContact(managerId)
            if(!phoneNumber.isNullOrEmpty()){
                openDailer(phoneNumber, context)
            }else {
                makeText(context, "No phone number available for this hostel", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openDailer(phoneNumber: String, context: Context) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
        }
        context.startActivity(intent)
    }


}
