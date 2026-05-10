package com.campusnest1.groupq.viewmodel

import android.content.Context
import android.content.Intent
import android.widget.Toast.makeText
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusnest1.groupq.data.AuthRepository
import com.campusnest1.groupq.data.HostelRepository
import com.campusnest1.groupq.model.Booking
import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.model.Room
import com.campusnest1.groupq.model.Review
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class HostelViewModel(
    private val repository: HostelRepository,
    private val authRepository: AuthRepository
) : ViewModel() {


    var hostels by mutableStateOf<List<Hostel>>(emptyList())
        private set

    var savedHostels by mutableStateOf<List<Hostel>>(emptyList())
        private set

    var savedStatus = mutableStateMapOf<String, Boolean>()
        private set

    var bookingHistory = mutableStateOf<List<Booking>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var isNotificationsEnabled = mutableStateOf(true)
        private set

    val db = Firebase.firestore

    var currentHostel by mutableStateOf<Hostel?>(null)
        private set

    var currentRooms by mutableStateOf<List<Room>>(emptyList())
        private set

    var reviews by mutableStateOf<List<Review>>(emptyList())
        private set

    var isDescriptionExpanded by mutableStateOf(false)
        private set

    var selectedRoom by mutableStateOf<Room?>(null)
        private set

    var showBookingSheet by mutableStateOf(false)
        private set
    
    var showReviewDialog by mutableStateOf(false)
        private set
    
    val alreadyBookedForRoomType by derivedStateOf {
        val userId = authRepository.getCurrentUser()?.userId ?: return@derivedStateOf false
        val hId = currentHostel?.hostelId ?: return@derivedStateOf false
        val rType = selectedRoom?.type ?: return@derivedStateOf false
        
        bookingHistory.value.any { 
            it.userId == userId &&
            it.hostelId.trim().equals(hId.trim(), ignoreCase = true) &&
            it.roomType.trim().equals(rType.trim(), ignoreCase = true)
        }
    }

    var bookingConfirmed by mutableStateOf(false)
        private set
    
    fun fetchHostelsData(){
        viewModelScope.launch {
            isLoading.value = true
            hostels = repository.getHostels()
            loadSavedStatus()
            isLoading.value = false
        }
    }

    private suspend fun loadSavedStatus() {
        val userId = authRepository.getCurrentUser()?.userId ?: return
        hostels.forEach { hostel ->
            savedStatus[hostel.hostelId] = repository.isHostelSaved(userId, hostel.hostelId)
        }
    }

    fun fetchHostelDetails(hostelId: String) {
        viewModelScope.launch {
            isLoading.value = true
            currentHostel = repository.getHostelById(hostelId)

            currentRooms = repository.getRoomsForHostel(hostelId)
            reviews = repository.getReviewsForHostel(hostelId)

            selectedRoom = currentRooms.firstOrNull { it.isAvailable && !it.status.contains("Full", ignoreCase = true) }
            
            // Refresh booking history to ensure 'already booked' logic works
            val userId = authRepository.getCurrentUser()?.userId ?: ""
            if (userId.isNotEmpty()) {
                bookingHistory.value = repository.getBookingHistory(userId)
            }

            
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
    
    fun updateShowReviewDialog(show: Boolean) {
        showReviewDialog = show
    }
    
    fun clearHistory() {
        val userId = authRepository.getCurrentUser()?.userId ?: return
        viewModelScope.launch {
            val success = repository.clearBookingHistory(userId)
            if (success) {
                bookingHistory.value = emptyList()
            }
        }
    }

    fun confirmBooking(date: String, time: String) {
        val userId = authRepository.getCurrentUser()?.userId ?: return
        val hostel = currentHostel ?: return
        val room = selectedRoom ?: return
        
        viewModelScope.launch {
            val newBooking = Booking(
                hostelId = hostel.hostelId,
                roomType = room.type,
                hostelName = hostel.name.ifBlank { "Unnamed Hostel" },
                hostelLocation = hostel.location,
                hostelImageUrl = hostel.imageUrl,
                userId = userId,
                date = date,
                time = time,
                status = "Confirmed"
            )
            
            val success = repository.createBooking(newBooking)
            if (success) {
                //Refresh booking history - alreadyBookedForRoomType updates automatically
                bookingHistory.value = repository.getBookingHistory(userId)
                bookingConfirmed = true
            }
        }
    }
    
    fun loadStudentData(forceRefresh: Boolean = false) {
        val userId = authRepository.getCurrentUser()?.userId ?: ""
        if (userId.isEmpty()) return

        val showLoading = forceRefresh || (savedHostels.isEmpty() && bookingHistory.value.isEmpty())

        viewModelScope.launch {
            if (showLoading) {
                isLoading.value = true
            }

            try {
                val savedTask = async { repository.getSavedHostels(userId) }
                val bookingTask = async { repository.getBookingHistory(userId) }

                savedHostels = savedTask.await()
                bookingHistory.value = bookingTask.await()
            } finally {
                isLoading.value = false
            }
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
        val userId = authRepository.getCurrentUser()?.userId ?: return
        val current = savedStatus[hostelId] ?: false
        val newState = !current
        savedStatus[hostelId] = newState //Update UI immediately
        viewModelScope.launch {
            try {
                repository.toggleSavedHostel(userId, hostelId)
                //Refresh the savedHostels list silently in background
                savedHostels = repository.getSavedHostels(userId)
            } catch (e: Exception) {
                //If Firestore fails, revert back to original state
                savedStatus[hostelId] = current
            }
        }
    }

    fun checkIfSaved(hostelId: String) {
        val userId = authRepository.getCurrentUser()?.userId ?: return
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

    fun submitReview(rating: Double, comment: String, userName: String) {
        val userId = authRepository.getCurrentUser()?.userId ?: return
        val hostelId = currentHostel?.hostelId ?: return
        
        viewModelScope.launch {
            val review = Review(
                userId = userId,
                userName = userName,
                hostelId = hostelId,
                rating = rating,
                comment = comment
            )
            val success = repository.addReview(review)
            if (success) {
                reviews = repository.getReviewsForHostel(hostelId)
                currentHostel = repository.getHostelById(hostelId)
            }
        }
    }
}
